-- Схема базы messages_db: сообщения и подписки.

CREATE TABLE messages (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    author_id   BIGINT      NOT NULL,
    text        TEXT        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at  TIMESTAMPTZ,

    CONSTRAINT chk_messages_author CHECK (author_id > 0),
    CONSTRAINT chk_messages_text   CHECK (btrim(text) <> '' AND char_length(text) <= 5000)
);

CREATE INDEX idx_messages_author_created ON messages (author_id, created_at DESC, id DESC);
CREATE INDEX idx_messages_created        ON messages (created_at DESC, id DESC);

CREATE TABLE subscriptions (
    subscriber_id  BIGINT      NOT NULL,
    author_id      BIGINT      NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (subscriber_id, author_id),
    CONSTRAINT chk_subscriptions_ids      CHECK (subscriber_id > 0 AND author_id > 0),
    CONSTRAINT chk_subscriptions_not_self CHECK (subscriber_id <> author_id)
);

CREATE INDEX idx_subscriptions_author ON subscriptions (author_id);
