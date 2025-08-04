package com.ecommerce.ecommercebackend.security.store;

import com.ecommerce.ecommercebackend.entity.StoreEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@RequiredArgsConstructor
public class StorePrincipal implements UserDetails {
    private final StoreEntity store;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_STORE"));
    }


    @Override
    public String getPassword() {
        return store.getPassword();
    }

    @Override
    public String getUsername() {
        return store.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public StoreEntity getStore() {
        return store;
    }
}