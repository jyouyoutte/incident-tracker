package com.incident.tracker.application.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    public String username;
    public String password;
    public String role;
}
