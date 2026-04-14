package com.dropbox.filesystem.controller;

import com.dropbox.filesystem.dto.CreateShareRequest;
import com.dropbox.filesystem.model.ShareLink;
import com.dropbox.filesystem.service.SharingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shares")
public class ShareController {

    private final SharingService sharingService;

    public ShareController(SharingService sharingService) {
        this.sharingService = sharingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShareLink create(@Valid @RequestBody CreateShareRequest request) {
        return sharingService.create(
                request.fileId(),
                request.ownerId(),
                request.permission(),
                request.expirySeconds()
        );
    }
}
