package com.incident.tracker.mapper;

import com.incident.tracker.application.dto.auth.RoleDto;
import com.incident.tracker.domain.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public Role dtoToEntity(RoleDto dto) {
        if(dto == null) {
            return null;
        }
        Role role= new Role();
        role.setId(dto.id());
        role.setName(dto.name());
        return role;
    }

    public RoleDto entityToDto(Role entity) {
        if(entity == null) {
            return null;
        }
        return new RoleDto(entity.getId(), entity.getName());
    }
}
