package com.dropbox.filesystem.service;

import com.dropbox.filesystem.model.FileMetadata;
import com.dropbox.filesystem.model.UploadSession;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadService {

    private final Map<String, UploadSession> sessions = new ConcurrentHashMap<>();
    private final FileMetadataService metadataService;
    private final S3PresignedUrlService presignedUrlService;

    public UploadService(FileMetadataService metadataService, S3PresignedUrlService presignedUrlService) {
        this.metadataService = metadataService;
        this.presignedUrlService = presignedUrlService;

    public UploadService(FileMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public UploadSession init(String userId, String fileName, long size, int chunkSizeBytes) {
        int totalChunks = (int) Math.ceil((double) size / chunkSizeBytes);
        String sessionId = UUID.randomUUID().toString();
        List<String> chunkUrls = new ArrayList<>();

        for (int i = 0; i < totalChunks; i++) {
            String partKey = String.format("uploads/%s/%s/part-%d-%s", userId, sessionId, i, fileName);
            chunkUrls.add(presignedUrlService.generateUploadUrl(partKey));
            chunkUrls.add("https://storage.example.com/upload/" + sessionId + "/parts/" + i + "?signature=mock");
        }

        UploadSession session = new UploadSession(
                sessionId,
                userId,
                fileName,
                size,
                totalChunks,
                chunkUrls,
                Instant.now()
        );

        sessions.put(sessionId, session);
        return session;
    }

    public FileMetadata complete(String sessionId, String checksum, List<String> etags, String path) {
        UploadSession session = sessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Upload session not found: " + sessionId);
        }
        if (etags.size() != session.totalChunks()) {
            throw new IllegalArgumentException("ETag count mismatch. Expected " + session.totalChunks());
        }

        FileMetadata metadata = metadataService.create(
                session.userId(),
                session.fileName(),
                path,
                session.size(),
                checksum
        );

        sessions.remove(sessionId);
        return metadata;
    }
}
