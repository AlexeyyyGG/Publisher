package com.cloud.publishing.common.dto.response;

public record ErrorResponse(
        int status,
        String error,
        String message
) {
}