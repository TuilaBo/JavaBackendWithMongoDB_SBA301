package controller;

import dto.ai.RagIngestResponse;
import dto.ai.RagQaRequest;
import dto.ai.RagQaResponse;
import dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ai.AiRagService;

// RAG disabled on Render until GEMINI / GCP credentials are configured
// @RestController
// @RequestMapping("/api/ai/rag")
public class AiRagController {

    private final AiRagService aiRagService;

    public AiRagController(AiRagService aiRagService) {
        this.aiRagService = aiRagService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<ApiResponse<RagIngestResponse>> ingest() {
        RagIngestResponse data = aiRagService.ingestKnowledge();
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "RAG ingest completed", data));
    }

    @PostMapping("/qa")
    public ResponseEntity<ApiResponse<RagQaResponse>> qa(@RequestBody RagQaRequest request) {
        RagQaResponse data = aiRagService.answerQuestion(request.getQuestion());
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "RAG answer generated", data));
    }
}

