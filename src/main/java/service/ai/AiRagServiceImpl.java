package service.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ai.RagIngestResponse;
import dto.ai.RagQaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pojo.RagChunk;
import repository.RagChunkRepository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AiRagServiceImpl implements AiRagService {

    private static final Logger log = LoggerFactory.getLogger(AiRagServiceImpl.class);
    private static final String KNOWLEDGE_GLOB = "classpath*:ai/knowledge/*.md";

    private final RagChunkRepository ragChunkRepository;
    private final EmbeddingModel embeddingModel;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final int topK;

    public AiRagServiceImpl(
            RagChunkRepository ragChunkRepository,
            EmbeddingModel embeddingModel,
            ChatClient.Builder chatClientBuilder,
            ObjectMapper objectMapper,
            @Value("${orchid.ai.rag.top-k:4}") int topK) {
        this.ragChunkRepository = ragChunkRepository;
        this.embeddingModel = embeddingModel;
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
        this.topK = topK;
    }

    @Override
    public RagIngestResponse ingestKnowledge() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(KNOWLEDGE_GLOB);

            List<RagChunk> chunksToSave = new ArrayList<>();
            for (Resource resource : resources) {
                String source = resource.getFilename() == null ? "unknown" : resource.getFilename();
                String text = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                List<RagChunkSeed> chunkSeeds = chunkByParagraph(source, text);
                for (RagChunkSeed chunkSeed : chunkSeeds) {
                    List<Double> embedding = toDoubleList(embeddingModel.embed(chunkSeed.content()));
                    RagChunk chunk = ragChunkRepository
                            .findBySourceAndChunkIndex(source, chunkSeed.chunkIndex())
                            .orElseGet(RagChunk::new);
                    chunk.setSource(source);
                    chunk.setChunkIndex(chunkSeed.chunkIndex());
                    chunk.setContent(chunkSeed.content());
                    chunk.setEmbeddingJson(objectMapper.writeValueAsString(embedding));
                    chunksToSave.add(chunk);
                }
            }

            if (!chunksToSave.isEmpty()) {
                ragChunkRepository.saveAll(chunksToSave);
            }

            log.info("RAG ingest completed. files={} chunks={}", resources.length, chunksToSave.size());
            return new RagIngestResponse(resources.length, chunksToSave.size());
        } catch (Exception e) {
            throw new RuntimeException("RAG ingest failed", e);
        }
    }

    @Override
    public RagQaResponse answerQuestion(String question) {
        if (!StringUtils.hasText(question)) {
            throw new IllegalArgumentException("question is required");
        }

        List<Document> matches = searchTopK(question);

        String context = buildContext(matches);
        Set<String> sources = extractSources(matches);

        String answer = chatClient.prompt()
                .system("You are an orchid e-commerce assistant. Answer only from provided context. " +
                        "If context is not enough, say you do not have enough context.")
                .user("Context:\n" + context + "\n\nQuestion:\n" + question)
                .call()
                .content();

        return new RagQaResponse(answer, matches == null ? 0 : matches.size(), new ArrayList<>(sources));
    }

    private List<RagChunkSeed> chunkByParagraph(String source, String text) {
        List<RagChunkSeed> chunks = new ArrayList<>();
        if (!StringUtils.hasText(text)) {
            return chunks;
        }

        String[] parts = text.split("\\r?\\n\\s*\\r?\\n");
        int index = 0;
        for (String part : parts) {
            if (!StringUtils.hasText(part)) {
                continue;
            }
            chunks.add(new RagChunkSeed(source, index, part.trim()));
            index++;
        }
        return chunks;
    }

    private List<Document> searchTopK(String question) {
        float[] questionEmbedding = embeddingModel.embed(question);
        List<RagChunk> allChunks = ragChunkRepository.findAll();
        if (allChunks.isEmpty()) {
            return List.of();
        }

        return allChunks.stream()
                .map(chunk -> new ScoredChunk(chunk, cosineSimilarity(questionEmbedding, parseEmbedding(chunk.getEmbeddingJson()))))
                .sorted(Comparator.comparingDouble(ScoredChunk::score).reversed())
                .limit(Math.max(1, topK))
                .map(scored -> new Document(
                        scored.chunk().getContent(),
                        Map.of(
                                "source", scored.chunk().getSource(),
                                "chunkIndex", String.valueOf(scored.chunk().getChunkIndex()),
                                "score", String.valueOf(scored.score())
                        )))
                .toList();
    }

    private float[] parseEmbedding(String embeddingJson) {
        try {
            List<Double> values = objectMapper.readValue(embeddingJson, new TypeReference<List<Double>>() {});
            float[] result = new float[values.size()];
            for (int i = 0; i < values.size(); i++) {
                result[i] = values.get(i).floatValue();
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse stored embedding", e);
        }
    }

    private List<Double> toDoubleList(float[] vector) {
        List<Double> values = new ArrayList<>(vector.length);
        for (float v : vector) {
            values.add((double) v);
        }
        return values;
    }

    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length == 0 || b.length == 0 || a.length != b.length) {
            return -1.0;
        }
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0.0 || normB == 0.0) {
            return -1.0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private String buildContext(List<Document> docs) {
        if (docs == null || docs.isEmpty()) {
            return "No relevant context found.";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < docs.size(); i++) {
            Document doc = docs.get(i);
            sb.append("[").append(i + 1).append("] ")
                    .append("(source=").append(doc.getMetadata().getOrDefault("source", "unknown")).append(")\n")
                    .append(doc.getText()).append("\n\n");
        }
        return sb.toString().trim();
    }

    private Set<String> extractSources(List<Document> docs) {
        Set<String> sources = new LinkedHashSet<>();
        if (docs == null) {
            return sources;
        }
        for (Document doc : docs) {
            Object source = doc.getMetadata().get("source");
            if (source != null) {
                sources.add(String.valueOf(source));
            }
        }
        return sources;
    }

    private record RagChunkSeed(String source, int chunkIndex, String content) {
    }

    private record ScoredChunk(RagChunk chunk, double score) {
    }
}
