package dto.ai;

import java.util.List;

public class RagQaResponse {
    private String answer;
    private int contextChunks;
    private List<String> sources;

    public RagQaResponse() {
    }

    public RagQaResponse(String answer, int contextChunks, List<String> sources) {
        this.answer = answer;
        this.contextChunks = contextChunks;
        this.sources = sources;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getContextChunks() {
        return contextChunks;
    }

    public void setContextChunks(int contextChunks) {
        this.contextChunks = contextChunks;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }
}

