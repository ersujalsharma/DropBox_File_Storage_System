package com.dropbox.filesystem.dto;

import java.util.List;

public record UploadInitResponse(
        String sessionId,
        int totalChunks,
        List<String> chunkUrls
) {
}
