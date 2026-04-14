package com.incident.tracker.incident.infrastructure.web.mapper;

import com.incident.tracker.incident.application.dto.IncidentDto;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentPatchRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentResponseVo;
import org.springframework.stereotype.Component;

@Component
public class IncidentWebMapper {
    public IncidentDto fromCreateRequestToDto(IncidentRequestVo vo) {
        if (vo == null) return null;
        return IncidentDto.builder()
                .title(vo.getTitle()).priority( vo.getPriority()).description(vo.getDescription())
                .build();
    }

    public IncidentDto fromPatchRequestToDto(IncidentPatchRequestVo vo){
        if (vo == null) return null;
        return IncidentDto.builder()
                .title(vo.getTitle()). priority(vo.getPriority()).description(vo.getDescription())
                .incidentStatus(vo.getStatus()).assignedDeveloper(vo.getAssignedDeveloper())
                .build();
    }

    public IncidentResponseVo toWebResponse(IncidentDto dto) {
        if (dto == null) return null;
        return new IncidentResponseVo(
                dto.getId(),
                dto.getTitle(),
                dto.getDescription(),
                dto.getPriority(),
                dto.getIncidentStatus(),
                dto.getCreatedAt(),
                dto.getUpdatedAt()
        );
    }
}
