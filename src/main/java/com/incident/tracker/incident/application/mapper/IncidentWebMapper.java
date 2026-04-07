package com.incident.tracker.incident.application.mapper;

import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentResponseVO;
import org.springframework.stereotype.Component;

@Component
public class IncidentWebMapper {
    public IncidentResponseVO toResponse(Incident incident) {
        return new IncidentResponseVO(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getPriority().getLabel(),
                incident.getIncidentStatus().name(),
                incident.getCreatedAt(),
                incident.getUpdatedAt()
        );
    }
}
