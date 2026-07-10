package com.cloud.publishing.backend.security;

import static com.cloud.publishing.backend.security.SecurityConstants.ROLE_CHIEF_EDITOR;

import com.cloud.publishing.model.employee.Type;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class EmployeeDetails implements UserDetails {
    private final Integer id;
    private final String email;
    private final String password;
    private final boolean chiefEditor;
    private final Type type;
    private static final String ROLE_JOURNALIST = "ROLE_JOURNALIST";
    private static final String ROLE_EDITOR = "ROLE_EDITOR";

    public EmployeeDetails(
            Integer id,
            String email,
            String password,
            boolean chiefEditor,
            Type type
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.chiefEditor = chiefEditor;
        this.type = type;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (chiefEditor) {
            return List.of(new SimpleGrantedAuthority(ROLE_CHIEF_EDITOR));
        }
        if (type == Type.JOURNALIST) {
            return List.of(new SimpleGrantedAuthority(ROLE_JOURNALIST));
        }
        return List.of(new SimpleGrantedAuthority(ROLE_EDITOR));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Integer getId() {
        return id;
    }
}