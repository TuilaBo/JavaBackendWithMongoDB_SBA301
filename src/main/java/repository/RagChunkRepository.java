package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pojo.RagChunk;

import java.util.Optional;

public interface RagChunkRepository extends JpaRepository<RagChunk, Long> {
    Optional<RagChunk> findBySourceAndChunkIndex(String source, Integer chunkIndex);
}
