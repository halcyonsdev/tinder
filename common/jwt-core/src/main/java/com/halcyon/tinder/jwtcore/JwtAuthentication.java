package com.halcyon.tinder.jwtcore;

import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthentication implements Authentication {

    private boolean authenticated;
    private String phoneNumber;
    private String token;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptySet();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return phoneNumber;
    }

    @Override
    public Object getPrincipal() {
        return phoneNumber;
    }

    @Override
    public String getName() {
        return phoneNumber;
    }
}
