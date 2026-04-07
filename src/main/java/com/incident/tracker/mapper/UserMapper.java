package com.incident.tracker.mapper;

import com.incident.tracker.auth.application.dto.UserDto;
import com.incident.tracker.auth.infrastructure.persistence.entity.Role;
import com.incident.tracker.auth.infrastructure.persistence.entity.User;
import com.incident.tracker.auth.infrastructure.web.vo.UserVo;
import org.springframework.stereotype.Component;

/** * UserMapper is a component responsible for mapping between UserDto, User entity, and UserVo.
 * It provides methods to convert between these different representations of user data.
 */
@Component
public class UserMapper {
    public User dtoToEntity(UserDto dto) {
        if(dto == null) {
            return null;
        }
        return User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .roles(dto.getRoles() == null ? null : dto.getRoles().stream().map(name -> {
                    // Role will be resolved/attached by service; here create minimal Role with name
                    Role r = new Role();
                    r.setName(name);
                    return r;
                }).toList())
                .build();
    }

    public UserVo dtoToVo(UserDto dto) {
        if(dto == null) {
            return null;
        }
        return new UserVo(dto.getUsername(), dto.getRoles());
    }

    public UserDto entityToDto(User user) {
        if(user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setRoles(user.getRoles() == null ? null : user.getRoles().stream().map(Role::getName).toList());
        return dto;
    }

    public UserDto voToDto(UserVo vo) {
        if(vo == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setUsername(vo.username());
        dto.setPassword(vo.password());
        dto.setRoles(vo.roles());
        return dto;
    }
}
