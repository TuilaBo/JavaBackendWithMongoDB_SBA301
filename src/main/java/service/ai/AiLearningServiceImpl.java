package service.ai;

import dto.ai.RewriteDescriptionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AiLearningServiceImpl implements AiLearningService {

    private final ChatClient chatClient;

    public AiLearningServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String ask(String message) {
        if (!StringUtils.hasText(message)) {
            throw new IllegalArgumentException("Message is required");
        }

        return chatClient.prompt()
                .system("You are an assistant for an e-commerce backend that sells orchid flowers. Important:\n" +
                        "- \"Orchid\" means orchid flower products, categories, and descriptions.\n" +
                        "- Do not talk about Laravel Orchid platform or unrelated software.\n" +
                        "- Keep response concise, practical, and product-focused.\n")
                .user(message)
                .call()
                .content();
    }

    @Override
    public RewriteDescriptionResponse rewriteDescription(String orchidName, String rawDescription) {
        if (!StringUtils.hasText(orchidName)) {
            throw new IllegalArgumentException("orchidName is required");
        }
        if (!StringUtils.hasText(rawDescription)) {
            throw new IllegalArgumentException("rawDescription is required");
        }

        RewriteDescriptionResponse response = chatClient.prompt()
                .system("You are an e-commerce copywriter for orchid flower products.\n" +
                        "Return only business-friendly content for orchid flowers.\n" +
                        "Output must match the target JSON object fields exactly.")
                .user("Rewrite the product description in Vietnamese.\n" +
                        "orchidName: " + orchidName + "\n" +
                        "rawDescription: " + rawDescription + "\n" +
                        "Requirements:\n" +
                        "- title: concise and sales-friendly\n" +
                        "- shortDescription: 35-60 words\n" +
                        "- seoKeywords: exactly 5 short keywords")
                .call()
                .entity(RewriteDescriptionResponse.class);

        if (response == null) {
            throw new IllegalStateException("AI returned empty structured response");
        }
        return response;
    }
}
