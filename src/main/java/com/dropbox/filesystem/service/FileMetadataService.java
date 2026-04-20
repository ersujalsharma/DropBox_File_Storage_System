package com.dropbox.filesystem.service;

import com.dropbox.filesystem.model.FileMetadata;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileMetadataService {

    private final Map<String, FileMetadata> metadataStore = new ConcurrentHashMap<>();

    public FileMetadata create(String userId, String fileName, String path, long size, String checksum) {
        String fileId = UUID.randomUUID().toString();
        FileMetadata metadata = new FileMetadata(
                fileId,
                userId,
                fileName,
                path,
                size,
                checksum,
                1,
                Instant.now()
        );
        metadataStore.put(fileId, metadata);
        return metadata;
    }

    public Optional<FileMetadata> findById(String fileId) {
        return Optional.ofNullable(metadataStore.get(fileId));
    }

    public List<FileMetadata> listByUser(String userId) {
        return metadataStore.values().stream()
                .filter(file -> file.userId().equals(userId))
                .toList();
    }
}
