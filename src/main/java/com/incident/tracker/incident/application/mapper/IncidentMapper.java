package com.incident.tracker.incident.application.mapper;

import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.model.Priority;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.PriorityEntity;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentPatchRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentResponseVo;
import com.incident.tracker.shared.domain.EnumFinderUtils;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public Incident toDomain(IncidentRequestVo vo) {
        return new Incident(
                vo.getTitle(),
                vo.getDescription(),
                EnumFinderUtils.parseByValue(Priority.class, vo.getPriority())
        );
    }

    public IncidentEntity toEntity(IncidentRequestVo dto) {
        return IncidentEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priorityEntity(EnumFinderUtils.parseByValue(PriorityEntity.class, dto.getPriority()))
                .incidentStatusEntity(EnumFinderUtils.parseByName(IncidentStatusEntity.class, dto.getStatus()))
                .build();
    }

    public IncidentResponseVo toResponse(Incident incident) {
        return new IncidentResponseVo(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getPriority()!=null ? incident.getPriority().getLabel() : null,
                incident.getIncidentStatus()!=null ? incident.getIncidentStatus().name() : null,
                incident.getCreatedAt(),
                incident.getUpdatedAt()
        );
    }

    /**
     * Updates entity fields from request DTO (only non-null values are applied)
     */
    public void updateEntityFromDto(IncidentPatchRequestVo dto, IncidentEntity entity) {
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
            entity.setPriorityEntity(EnumFinderUtils.parseByValue(PriorityEntity.class, dto.getPriority()));
        }

        if(dto.getStatus() !=null){
            entity.setIncidentStatusEntity(EnumFinderUtils.parseByName(IncidentStatusEntity.class, dto.getStatus()));
        }

        if (dto.getAssignedDeveloper() != null) {
            entity.setAssignedDeveloper(dto.getAssignedDeveloper());
        }
    }

}
