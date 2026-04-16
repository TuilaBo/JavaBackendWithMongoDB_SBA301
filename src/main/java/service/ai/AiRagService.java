package service.ai;

import dto.ai.RagIngestResponse;
import dto.ai.RagQaResponse;

public interface AiRagService {
    RagIngestResponse ingestKnowledge();
    RagQaResponse answerQuestion(String question);
}
