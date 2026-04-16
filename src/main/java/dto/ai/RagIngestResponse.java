package dto.ai;

public class RagIngestResponse {
    private int sourceFiles;
    private int chunks;

    public RagIngestResponse() {
    }

    public RagIngestResponse(int sourceFiles, int chunks) {
        this.sourceFiles = sourceFiles;
        this.chunks = chunks;
    }

    public int getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(int sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }
}

