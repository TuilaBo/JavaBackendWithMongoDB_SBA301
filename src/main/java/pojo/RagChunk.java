package pojo;

import jakarta.persistence.*;

@Entity
@Table(
        name = "rag_chunk",
        uniqueConstraints = @UniqueConstraint(name = "uk_rag_chunk_source_index", columnNames = {"source", "chunk_index"})
)
public class RagChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String source;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Lob
    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    private String content;

    @Lob
    @Column(name = "embedding_json", nullable = false, columnDefinition = "nvarchar(max)")
    private String embeddingJson;

    public RagChunk() {
    }

    public RagChunk(Long id, String source, Integer chunkIndex, String content, String embeddingJson) {
        this.id = id;
        this.source = source;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.embeddingJson = embeddingJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmbeddingJson() {
        return embeddingJson;
    }

    public void setEmbeddingJson(String embeddingJson) {
        this.embeddingJson = embeddingJson;
    }
}
