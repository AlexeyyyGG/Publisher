package com.cloud.publishing.backend.security;

import static com.cloud.publishing.backend.security.SecurityConstants.ROLE_CHIEF_EDITOR;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils() {
    }

    public static UserPrincipal currentUser() {
        return (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static boolean isChiefEditor() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_CHIEF_EDITOR));
    }
}