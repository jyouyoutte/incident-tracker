package com.incident.tracker.mapper;

import com.incident.tracker.incident.infrastructure.web.vo.IncidentPatchRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentResponseVO;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.PriorityEntity;
import com.incident.tracker.shared.domain.EnumFinderUtils;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentEntity toEntity(IncidentRequestVo dto) {
        return IncidentEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priorityEntity(EnumFinderUtils.parseByValue(PriorityEntity.class, dto.getPriority()))
                .incidentStatusEntity(EnumFinderUtils.parseByName(IncidentStatusEntity.class, dto.getStatus()))
                .build();
    }

    public IncidentResponseVO toResponse(IncidentEntity incident) {
        return new IncidentResponseVO(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getPriorityEntity()!=null ? incident.getPriorityEntity().getLabel() : null,
                incident.getIncidentStatusEntity()!=null ? incident.getIncidentStatusEntity().name() : null,
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
