package com.cloud.publishing.backend.dto;

public record EmployeeListItem(
        String firstName,
        String lastName,
        String email,
        boolean chiefEditor
) {
}