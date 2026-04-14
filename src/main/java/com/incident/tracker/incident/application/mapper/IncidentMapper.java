package com.incident.tracker.incident.application.mapper;

import com.incident.tracker.incident.application.dto.IncidentDto;
import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.model.IncidentStatus;
import com.incident.tracker.incident.domain.model.Priority;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentPatchRequestVo;
import com.incident.tracker.shared.domain.EnumFinderUtils;
import org.springframework.stereotype.Component;


@Component
public class IncidentMapper {

    public Incident toDomain(IncidentDto dto) {
        if (dto == null) return null;
        Priority priority = null;
        if (dto.getPriority() != null) {
            priority = EnumFinderUtils.parseByValue(Priority.class, dto.getPriority());
        }
        Incident incident = new Incident(dto.getTitle(), dto.getDescription(), priority);
        if (dto.getIncidentStatus() != null) {
            IncidentStatus status = EnumFinderUtils.parseByName(IncidentStatus.class, dto.getIncidentStatus());
            if (status != null) incident.setIncidentStatus(status);
        }
        return incident;
    }

    public IncidentDto toDto(Incident incident) {
        if (incident == null) return null;
        return  IncidentDto.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .priority(incident.getPriority()!=null ? incident.getPriority().getLabel() : null)
                .incidentStatus(incident.getIncidentStatus()!=null ? incident.getIncidentStatus().name() : null)
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }

    /**
     * Apply patch request values to the domain incident (only non-null values are applied).
     * Persistence-level mapping (entity fields) should be handled by the persistence mapper.
     */
    public void assignResponsible(IncidentPatchRequestVo vo, Incident domain) {
        if (vo == null || domain == null) return;
        domain.update(
                vo.getTitle(),
                vo.getDescription(),
                vo.getPriority(),
                vo.getStatus()
        );
        if (vo.getAssignedDeveloper() != null) {
            domain.assignedResponsible(vo.getAssignedDeveloper());
        }
    }

}
