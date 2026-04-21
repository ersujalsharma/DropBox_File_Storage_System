package com.dropbox.filesystem.model;

import java.time.Instant;
import java.util.List;

public record UploadSession(
        String sessionId,
        String userId,
        String fileName,
        long size,
        int totalChunks,
        List<String> chunkUrls,
        Instant createdAt
) {
}
