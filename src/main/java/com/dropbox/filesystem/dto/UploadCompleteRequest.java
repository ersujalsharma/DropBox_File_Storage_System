package com.dropbox.filesystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UploadCompleteRequest(
        @NotBlank String sessionId,
        @NotBlank String checksum,
        @NotEmpty List<String> etags,
        @NotBlank String path
) {
}
