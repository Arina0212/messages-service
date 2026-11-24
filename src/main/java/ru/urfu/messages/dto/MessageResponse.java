package ru.urfu.messages.dto;

import java.time.Instant;

public class MessageResponse {

    private final Long id;
    private final Long authorId;
    private final String text;
    private final UserSummary author;
    private final Instant createdAt;
    private final Instant updatedAt;

    public MessageResponse(
            Long id,
            Long authorId,
            String text,
            UserSummary author,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.authorId = authorId;
        this.text = text;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getText() {
        return text;
    }

    public UserSummary getAuthor() {
        return author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
