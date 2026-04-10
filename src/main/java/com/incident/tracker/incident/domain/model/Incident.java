package com.incident.tracker.incident.domain.model;

import com.incident.tracker.incident.domain.exception.IncidentAlreadyClosedException;
import com.incident.tracker.shared.domain.EnumFinderUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Incident {
    @Setter
    private Long id;
    private String title;
    private String description;
    @Setter
    private IncidentStatus incidentStatus;
    @Setter
    private Priority priority;
    private String assignedResponsible;
    @Setter
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;
    private final List<Comment> comments = new ArrayList<>();

    // Constructeur métier
    public Incident(String title, String description, Priority priority) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is mandatory");
        }
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.incidentStatus = IncidentStatus.OPEN;
    }

    /**
     * Static factory to reconstitute an Incident aggregate from persistence without running business validations.
     * Intended for mappers/repositories only.
     */
    public static Incident reconstitute(Long id,
                                       String title,
                                       String description,
                                       Priority priority,
                                       IncidentStatus incidentStatus,
                                       String assignedResponsible,
                                       List<Comment> comments) {
        Incident incident = new Incident(title, description, priority);
        incident.id = id;
        incident.incidentStatus = incidentStatus != null ? incidentStatus : IncidentStatus.OPEN;
        incident.assignedResponsible = assignedResponsible;
        if (comments != null && !comments.isEmpty()) {
            incident.comments.addAll(comments);
        }
        return incident;
    }

    public List<Comment> getComments() {
        return List.copyOf(comments);
    }

    // 🔥 domain methods

    public void close() {
        if (this.incidentStatus == IncidentStatus.CLOSED) {
            throw new IncidentAlreadyClosedException(this.id);
        }
        this.incidentStatus = IncidentStatus.CLOSED;
    }

    public void update(String title, String description, String priorityLabel, String incidentStatusLabel) {
        if (this.incidentStatus == IncidentStatus.CLOSED) {
            throw new IncidentAlreadyClosedException(this.id);
        }

        if (title != null && !title.isBlank()) {
            this.title = title;
        }

        if (description != null) {
            this.description = description;
        }

        Priority priority = EnumFinderUtils.parseByValue(Priority.class, priorityLabel);
        if (priority !=null) {
            this.priority = priority;
        }
        IncidentStatus incidentStatus = EnumFinderUtils.parseByName(IncidentStatus.class, incidentStatusLabel);
        if (incidentStatus !=null) {
            this.incidentStatus = incidentStatus;
        }
    }



    public void assignedResponsible(String responsible) {
        this.assignedResponsible = responsible;
    }

    public void addComment(String content, String author) {
        if (this.incidentStatus == IncidentStatus.CLOSED) {
            throw new IncidentAlreadyClosedException(this.id);
        }

        Comment comment = new Comment(content, author);
        comments.add(comment);
    }

    /**
     * Add a comment bypassing business validation. Intended for reconstitution from persistence only.
     */
    public void restoreComment(Comment comment) {
        if (comment == null) return;
        this.comments.add(comment);
    }

    public void create() {
        this.incidentStatus = IncidentStatus.OPEN;
    }
}
