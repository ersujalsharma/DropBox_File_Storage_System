package com.dropbox.filesystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UploadInitRequest(
        @NotBlank String userId,
        @NotBlank String fileName,
        @Min(1) long size,
        @Min(1) int chunkSizeBytes
) {
}
