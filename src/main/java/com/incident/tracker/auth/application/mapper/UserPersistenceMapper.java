package com.incident.tracker.auth.application.mapper;

import com.incident.tracker.auth.application.dto.UserDto;
import com.incident.tracker.auth.infrastructure.persistence.entity.RoleEntity;
import com.incident.tracker.auth.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {
    public UserEntity dtoToEntity(UserDto dto) {
        if(dto == null) {
            return null;
        }
        return UserEntity.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .roles(dto.getRoles() == null ? null : dto.getRoles().stream().map(name -> {
                    // Role will be resolved/attached by service; here create minimal Role with name
                    RoleEntity r = new RoleEntity();
                    r.setName(name);
                    return r;
                }).toList())
                .build();
    }

    public UserDto entityToDto(UserEntity user) {
        if(user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setRoles(user.getRoles() == null ? null : user.getRoles().stream().map(RoleEntity::getName).toList());
        return dto;
    }
}
