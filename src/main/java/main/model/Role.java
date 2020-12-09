package main.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    USER(EnumSet.of(Permissions.USER)),
    MODERATOR(EnumSet.of(Permissions.USER, Permissions.MODERATE));



    private final Set<Permissions> permissions;

    Role(Set<Permissions> permissions) {
        this.permissions = permissions;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities(){
        return permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission())).collect(Collectors.toSet());
    }
}
