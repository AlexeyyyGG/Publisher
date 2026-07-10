package com.cloud.publishing.common.dto.response;

public record ReviewDetailsDTO(
        Integer id,
        String ReviewerName,
        String ReviewText
) {
}