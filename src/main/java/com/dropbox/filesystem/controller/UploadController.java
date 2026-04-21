package com.dropbox.filesystem.controller;

import com.dropbox.filesystem.dto.UploadCompleteRequest;
import com.dropbox.filesystem.dto.UploadInitRequest;
import com.dropbox.filesystem.dto.UploadInitResponse;
import com.dropbox.filesystem.model.FileMetadata;
import com.dropbox.filesystem.model.UploadSession;
import com.dropbox.filesystem.service.UploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "Upload", description = "Upload session lifecycle APIs")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/init")
    @ResponseStatus(HttpStatus.CREATED)
    public UploadInitResponse init(@Valid @RequestBody UploadInitRequest request) {
        UploadSession session = uploadService.init(
                request.userId(),
                request.fileName(),
                request.size(),
                request.chunkSizeBytes()
        );

        return new UploadInitResponse(session.sessionId(), session.totalChunks(), session.chunkUrls());
    }

    @PostMapping("/complete")
    public FileMetadata complete(@Valid @RequestBody UploadCompleteRequest request) {
        return uploadService.complete(request.sessionId(), request.checksum(), request.etags(), request.path());
    }
}
