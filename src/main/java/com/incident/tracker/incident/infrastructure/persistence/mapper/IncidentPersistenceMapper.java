package com.incident.tracker.incident.infrastructure.persistence.mapper;

import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.model.IncidentStatus;
import com.incident.tracker.incident.domain.model.Priority;
import com.incident.tracker.incident.domain.model.Comment;
import com.incident.tracker.incident.infrastructure.persistence.entity.CommentEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.PriorityEntity;
import com.incident.tracker.shared.domain.EnumFinderUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IncidentPersistenceMapper {
    public Incident toDomain(IncidentEntity entity) {
        if (entity == null) return null;
        Priority priority = null;
        if (entity.getPriorityEntity() != null) {
            priority = EnumFinderUtils.parseByName(Priority.class, entity.getPriorityEntity().name());
        }

        IncidentStatus status = null;
        if (entity.getIncidentStatusEntity() != null) {
            status = EnumFinderUtils.parseByName(IncidentStatus.class, entity.getIncidentStatusEntity().name());
        }

        // map comments into domain objects (preserve createdAt)
        List<Comment> domainComments = new ArrayList<>();
        if (entity.getComments() != null && !entity.getComments().isEmpty()) {
            for (CommentEntity ce : entity.getComments()) {
                Comment c = new Comment(ce.getContent(), ce.getAuthor(), ce.getCreatedAt());
                domainComments.add(c);
            }
        }

        // Reconstitute aggregate from persistence without running business validations
        Incident domain = Incident.reconstitute(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                priority,
                status,
                entity.getAssignedDeveloper(),
                domainComments
        );
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
        if (domain.getIncidentStatus() != null) {
            entity.setIncidentStatusEntity(EnumFinderUtils.parseByName(IncidentStatusEntity.class, domain.getIncidentStatus().name()));
        }
        if (domain.getPriority() != null) {
            entity.setPriorityEntity(EnumFinderUtils.parseByName(PriorityEntity.class, domain.getPriority().name()));
        }
        entity.setAssignedDeveloper(domain.getAssignedResponsible());

        // map comments from domain to entity
        List<CommentEntity> commentEntities = new ArrayList<>();
        if (domain.getComments() != null && !domain.getComments().isEmpty()) {
            for (Comment c : domain.getComments()) {
                CommentEntity ce = new CommentEntity();
                ce.setContent(c.getContent());
                ce.setAuthor(c.getAuthor());
                ce.setCreatedAt(c.getCreatedAt());
                ce.setIncident(entity);
                commentEntities.add(ce);
            }
        }
        entity.setComments(commentEntities);

        return entity;
    }
}
