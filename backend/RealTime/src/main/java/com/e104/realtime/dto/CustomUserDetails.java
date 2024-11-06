package com.e104.realtime.dto;

import com.e104.realtime.domain.User.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> "ROLE_USER");
        return collection;
    }

    @Override
    public String getPassword() {
        return "talkie";
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    public CustomUserDetails(User user) {
        this.user = user;
    }
}
