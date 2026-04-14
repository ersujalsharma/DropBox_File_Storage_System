package com.dropbox.filesystem.model;

import java.time.Instant;

public record FileMetadata(
        String fileId,
        String userId,
        String fileName,
        String path,
        long size,
        String checksum,
        int version,
        Instant createdAt
) {
}
