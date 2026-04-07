package com.incident.tracker.mapper;

import com.incident.tracker.auth.application.dto.AuthResponseDto;
import com.incident.tracker.auth.infrastructure.web.vo.AuthResponseVo;
import org.springframework.stereotype.Component;

@Component
public class AuthResponseMapper {
    public AuthResponseVo dtoToVo(AuthResponseDto dto) {
        if(dto == null) {
            return null;
        }
        return new AuthResponseVo(dto.token(), dto.type());
    }
}
