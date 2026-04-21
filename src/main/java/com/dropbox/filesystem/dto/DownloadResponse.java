package com.dropbox.filesystem.dto;

public record DownloadResponse(String fileId, String signedUrl, long expiresInSeconds) {
}
