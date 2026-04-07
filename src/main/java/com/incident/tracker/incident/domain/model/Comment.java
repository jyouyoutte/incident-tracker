package com.incident.tracker.incident.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

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
}
