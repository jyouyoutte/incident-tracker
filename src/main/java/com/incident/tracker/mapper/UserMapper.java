package com.incident.tracker.mapper;

import com.incident.tracker.application.dto.auth.UserDto;
import com.incident.tracker.domain.model.User;
import com.incident.tracker.infrastructure.web.vo.auth.UserVo;
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
                .role(dto.getRole())
                .build();
    }

    public UserVo dtoToVo(UserDto dto) {
        if(dto == null) {
            return null;
        }
        return new UserVo(dto.getUsername(), dto.getPassword(), dto.getRole());
    }

    public UserDto entityToDto(User user) {
        if(user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        return dto;
    }

    public UserDto voToDto(UserVo vo) {
        if(vo == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setUsername(vo.username());
        dto.setPassword(vo.password());
        dto.setRole(vo.role());
        return dto;
    }
}
