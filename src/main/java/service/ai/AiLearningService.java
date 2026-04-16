package service.ai;

import dto.ai.RewriteDescriptionResponse;

public interface AiLearningService {
    String ask(String message);
    RewriteDescriptionResponse rewriteDescription(String orchidName, String rawDescription);
}
