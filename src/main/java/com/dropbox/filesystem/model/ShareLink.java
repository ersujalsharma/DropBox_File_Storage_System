package com.dropbox.filesystem.model;

import java.time.Instant;

public record ShareLink(
        String shareId,
        String fileId,
        String ownerId,
        String token,
        String permission,
        Instant expiry
) {
}
