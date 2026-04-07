package com.incident.tracker.incident.domain.model;

import com.incident.tracker.incident.domain.exception.IncidentAlreadyClosedException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Incident {
    private Long id;
    private String title;
    private String description;
    private IncidentStatus incidentStatus;
    private Priority priority;
    private String assignedResponsible;
    private LocalDateTime createdAt;
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



    public List<Comment> getComments() {
        return List.copyOf(comments);
    }

    // 🔥 Méthodes métier

    public void close() {
        if (this.incidentStatus == IncidentStatus.CLOSED) {
            throw new IncidentAlreadyClosedException(this.id);
        }
        this.incidentStatus = IncidentStatus.CLOSED;
    }

    public void update(String title, String description, Priority priority) {
        if (this.incidentStatus == IncidentStatus.CLOSED) {
            throw new IncidentAlreadyClosedException(this.id);
        }

        if (title != null && !title.isBlank()) {
            this.title = title;
        }

        if (description != null) {
            this.description = description;
        }

        if (priority != null) {
            this.priority = priority;
        }
    }

    public void assignedResponsible(String responsible) {
        this.assignedResponsible = responsible;
    }

    public void addComment(String content, String author) {
        if (this.incidentStatus == IncidentStatus.CLOSED) {
            throw new IllegalStateException("Cannot add comment to closed incident");
        }

        Comment comment = new Comment(content, author);
        comments.add(comment);
    }
}
