package ru.urfu.messages.domain;

import java.time.Instant;

public class Message {

    private final Long id;
    private final Long authorId;
    private final String text;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Message(Long id, Long authorId, String text, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.authorId = authorId;
        this.text = text;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
