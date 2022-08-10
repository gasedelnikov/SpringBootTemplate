package com.gri.template.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gri.template.model.enums.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements UserDetails {
    private static final long serialVersionUID = 2396654715019746670L;
    private static int DESCRIPTION_MAX_LENGTH = 1000;

    private UUID id;
    private String phoneNumber;
    private String email;
    private UserStatus status;
    private String password;

    private String surname;
    private String name;
    private String description;

    private String username;
    private boolean enabled = true;

    private boolean isAdmin;

    public String getDescription() {
        if (description != null && description.length() > DESCRIPTION_MAX_LENGTH){
            return description.substring(0, DESCRIPTION_MAX_LENGTH - 3) + "...";
        }
        else {
            return description;
        }
    }

    @JsonIgnore
    public boolean isAdmin() {
        return isAdmin;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return (username != null) ? username: email;
    }

    @JsonIgnore
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}
