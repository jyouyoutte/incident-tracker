package com.incident.tracker.incident.infrastructure.persistence.mapper;

import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.model.IncidentStatus;
import com.incident.tracker.incident.domain.model.Priority;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.PriorityEntity;
import com.incident.tracker.shared.domain.EnumFinderUtils;
import org.springframework.stereotype.Component;

@Component
public class IncidentPersistenceMapper {
    public Incident toDomain(IncidentEntity entity) {
        Incident domain = new Incident(
                entity.getTitle(),
                entity.getDescription(),
                EnumFinderUtils.parseByName(Priority.class, entity.getPriorityEntity().name())
        );
        domain.setId(entity.getId());
        domain.setIncidentStatus(EnumFinderUtils.parseByName(IncidentStatus.class, entity.getIncidentStatusEntity().name()));
        domain.setAssignedResponsible(entity.getAssignedDeveloper());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        return domain;
    }

    public IncidentEntity toEntity(Incident domain) {
        if(domain == null){
            return null;
        }
        IncidentEntity entity = new IncidentEntity();

        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setIncidentStatusEntity(EnumFinderUtils.parseByName(IncidentStatusEntity.class, domain.getIncidentStatus().name()));
        entity.setPriorityEntity(EnumFinderUtils.parseByName(PriorityEntity.class, domain.getPriority().name()));

        return entity;
    }
}
