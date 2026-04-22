package com.dropbox.filesystem.service;

import com.dropbox.filesystem.model.ShareLink;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SharingService {

    private final Map<String, ShareLink> shareLinks = new ConcurrentHashMap<>();

    public ShareLink create(String fileId, String ownerId, String permission, long expirySeconds) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String shareId = UUID.randomUUID().toString();

        ShareLink shareLink = new ShareLink(
                shareId,
                fileId,
                ownerId,
                token,
                permission,
                Instant.now().plusSeconds(expirySeconds)
        );

        shareLinks.put(shareId, shareLink);
        return shareLink;
    }
}
