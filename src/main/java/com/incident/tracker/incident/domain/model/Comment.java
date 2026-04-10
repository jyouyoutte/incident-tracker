package com.incident.tracker.incident.domain.model;

import lombok.Getter;
import java.time.LocalDateTime;

/** */
@Getter
public class Comment {
    private final String content;
    private final String author;
    private final LocalDateTime createdAt;

    public Comment(String content, String author) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content is mandatory");
        }
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    /** Public constructor used by mappers to restore persisted comments with their original timestamp*/
    public Comment(String content, String author, LocalDateTime createdAt) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content is mandatory");
        }
        this.content = content;
        this.author = author;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
