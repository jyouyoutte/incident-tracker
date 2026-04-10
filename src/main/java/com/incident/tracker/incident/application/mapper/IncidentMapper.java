package com.incident.tracker.incident.application.mapper;

import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.model.IncidentStatus;
import com.incident.tracker.incident.domain.model.Priority;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentPatchRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentResponseVo;
import com.incident.tracker.shared.domain.EnumFinderUtils;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public Incident toDomain(IncidentRequestVo vo) {
        if (vo == null) return null;
        Priority priority = null;
        if (vo.getPriority() != null) {
            priority = EnumFinderUtils.parseByValue(Priority.class, vo.getPriority());
        }
        Incident incident = new Incident(vo.getTitle(), vo.getDescription(), priority);
        if (vo.getStatus() != null) {
            IncidentStatus status = EnumFinderUtils.parseByName(IncidentStatus.class, vo.getStatus());
            if (status != null) incident.setIncidentStatus(status);
        }
        return incident;
    }

    public IncidentResponseVo toResponse(Incident incident) {
        if (incident == null) return null;
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
