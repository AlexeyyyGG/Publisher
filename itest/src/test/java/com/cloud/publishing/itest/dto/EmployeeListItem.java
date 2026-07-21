package com.cloud.publishing.itest.dto;

public record EmployeeListItem(
        String firstName,
        String lastName,
        String email,
        boolean chiefEditor
) {
}