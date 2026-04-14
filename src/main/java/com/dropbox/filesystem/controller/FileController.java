package com.dropbox.filesystem.controller;

import com.dropbox.filesystem.dto.DownloadResponse;
import com.dropbox.filesystem.model.FileMetadata;
import com.dropbox.filesystem.service.DownloadService;
import com.dropbox.filesystem.service.FileMetadataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileMetadataService metadataService;
    private final DownloadService downloadService;

    public FileController(FileMetadataService metadataService, DownloadService downloadService) {
        this.metadataService = metadataService;
        this.downloadService = downloadService;
    }

    @GetMapping
    public List<FileMetadata> listByUser(@RequestParam String userId) {
        return metadataService.listByUser(userId);
    }

    @GetMapping("/{fileId}/download")
    public DownloadResponse downloadLink(@PathVariable String fileId) {
        return downloadService.signedUrlFor(fileId);
    }
}
