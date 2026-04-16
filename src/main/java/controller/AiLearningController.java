package controller;

import dto.ai.AiChatRequest;
import dto.ai.RewriteDescriptionRequest;
import dto.ai.RewriteDescriptionResponse;
import dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ai.AiLearningService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/step1")
public class AiLearningController {

    private final AiLearningService aiLearningService;

    public AiLearningController(AiLearningService aiLearningService) {
        this.aiLearningService = aiLearningService;
    }

    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ping() {
        Map<String, Object> data = new HashMap<>();
        data.put("phase", "step1");
        data.put("status", "ready");
        data.put("message", "Spring AI wiring is loaded");
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "AI step1 ready", data));
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<Map<String, Object>>> chat(@RequestBody AiChatRequest request) {
        String answer = aiLearningService.ask(request.getMessage());
        Map<String, Object> data = new HashMap<>();
        data.put("answer", answer);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "AI response generated", data));
    }

    @PostMapping("/rewrite-description")
    public ResponseEntity<ApiResponse<RewriteDescriptionResponse>> rewriteDescription(
            @RequestBody RewriteDescriptionRequest request) {
        RewriteDescriptionResponse data = aiLearningService.rewriteDescription(
                request.getOrchidName(),
                request.getRawDescription());
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "AI structured response generated", data));
    }
}

