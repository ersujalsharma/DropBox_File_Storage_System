package com.dropbox.filesystem.service;

import com.dropbox.filesystem.dto.DownloadResponse;
import com.dropbox.filesystem.model.FileMetadata;
import org.springframework.stereotype.Service;

@Service
public class DownloadService {

    private final FileMetadataService metadataService;
    private final S3PresignedUrlService presignedUrlService;

    public DownloadService(FileMetadataService metadataService, S3PresignedUrlService presignedUrlService) {
        this.metadataService = metadataService;
        this.presignedUrlService = presignedUrlService;

    public DownloadService(FileMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public DownloadResponse signedUrlFor(String fileId) {
        FileMetadata file = metadataService.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));

        String signedUrl = presignedUrlService.generateDownloadUrl(file.path());
        return new DownloadResponse(file.fileId(), signedUrl, presignedUrlService.downloadExpirySeconds());
        long expiry = 300;
        String signedUrl = "https://storage.example.com/download/" + file.fileId() + "?signature=mock";
        return new DownloadResponse(file.fileId(), signedUrl, expiry);
    }
}
