package com.incident.tracker.mapper;

import com.incident.tracker.application.dto.auth.AuthResponseDto;
import com.incident.tracker.infrastructure.web.vo.auth.AuthResponseVo;
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
