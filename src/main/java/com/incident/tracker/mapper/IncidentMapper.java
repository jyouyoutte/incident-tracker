package com.incident.tracker.mapper;

import com.incident.tracker.domain.IncidentRequestDto;
import com.incident.tracker.domain.IncidentResponseDto;
import com.incident.tracker.model.Incident;

public class IncidentMapper {
    public Incident toEntity(IncidentRequestDto dto){
        return Incident.builder()
                .title(dto.title())
                .description(dto.description())
                .build();
    }

    public IncidentResponseDto toResponseDto(Incident incident) {
        return new IncidentResponseDto(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getPriority().name(),
                incident.getStatus().name(),
                incident.getCreatedAt(),
                incident.getUpdatedAt()
        );
    }
}
