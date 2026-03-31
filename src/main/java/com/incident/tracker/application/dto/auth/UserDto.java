package com.incident.tracker.application.dto.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private List<String> roles;
}
