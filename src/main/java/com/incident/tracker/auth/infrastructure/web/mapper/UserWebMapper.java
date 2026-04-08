package com.incident.tracker.auth.infrastructure.web.mapper;

import com.incident.tracker.auth.application.dto.UserDto;
import com.incident.tracker.auth.infrastructure.web.vo.UserVo;
import org.springframework.stereotype.Component;


/** UserMapper is a component responsible for mapping between UserDto and UserVo.
 * It provides methods to convert between these different representations of user data.*/
@Component
public class UserWebMapper {

    public UserVo dtoToVo(UserDto dto) {
        if(dto == null) {
            return null;
        }
        return new UserVo(dto.getUsername(), dto.getRoles());
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