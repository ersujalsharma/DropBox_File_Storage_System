package com.dropbox.filesystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateShareRequest(
        @NotBlank String fileId,
        @NotBlank String ownerId,
        @NotBlank String permission,
        @Min(1) long expirySeconds
) {
}
