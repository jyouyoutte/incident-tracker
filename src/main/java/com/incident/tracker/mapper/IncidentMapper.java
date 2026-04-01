package com.incident.tracker.mapper;

import com.incident.tracker.incident.application.dto.IncidentPatchRequestDto;
import com.incident.tracker.incident.application.dto.IncidentRequestDto;
import com.incident.tracker.incident.application.dto.IncidentResponseDto;
import com.incident.tracker.incident.infrastructure.persistence.entity.Incident;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatus;
import com.incident.tracker.incident.infrastructure.persistence.entity.Priority;
import com.incident.tracker.domain.utils.EnumFinderUtils;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public Incident toEntity(IncidentRequestDto dto) {
        return Incident.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(EnumFinderUtils.parseByValue(Priority.class, dto.getPriority()))
                .status(EnumFinderUtils.parseByName(IncidentStatus.class, dto.getStatus()))
                .build();
    }

    public IncidentResponseDto toResponse(Incident incident) {
        return new IncidentResponseDto(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getPriority().getLabel(),
                incident.getStatus().name(),
                incident.getCreatedAt(),
                incident.getUpdatedAt()
        );
    }

    /**
     * Updates entity fields from request DTO (only non-null values are applied)
     */
    public void updateEntityFromDto(IncidentPatchRequestDto dto, Incident entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            entity.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            entity.setDescription(dto.getDescription());
        }

        if (dto.getPriority() != null) {
            entity.setPriority(EnumFinderUtils.parseByValue(Priority.class, dto.getPriority()));
        }

        if(dto.getStatus() !=null){
            entity.setStatus(EnumFinderUtils.parseByName(IncidentStatus.class, dto.getStatus()));
        }

        if (dto.getAssignedDeveloper() != null) {
            entity.setAssignedDeveloper(dto.getAssignedDeveloper());
        }
    }

}
