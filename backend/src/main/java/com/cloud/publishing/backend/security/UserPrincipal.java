package com.cloud.publishing.backend.security;

import java.security.Principal;

public record UserPrincipal(Integer id, String email) implements Principal {
    @Override
    public String getName() {
        return email;
    }
}