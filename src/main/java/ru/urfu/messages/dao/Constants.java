package ru.urfu.messages.dao;


public final class Constants {

    private Constants() {
    }

    private static final String MESSAGE_FIELDS =
            "m.id, m.author_id, m.text, m.created_at, m.updated_at";

    private static final String RETURNING_FIELDS =
            "id, author_id, text, created_at, updated_at";

    public static final String INSERT_MESSAGE = """
            INSERT INTO messages (author_id, text)
            VALUES (:authorId, :text)
            RETURNING %s""".formatted(RETURNING_FIELDS);

    public static final String FIND_MESSAGE_BY_ID = """
            SELECT %s
            FROM messages m
            WHERE m.id = :id AND m.deleted_at IS NULL""".formatted(MESSAGE_FIELDS);


    public static final String FIND_MESSAGES = """
            SELECT %s
            FROM messages m
            WHERE m.deleted_at IS NULL
              AND (CAST(:authorId AS BIGINT) IS NULL OR m.author_id = CAST(:authorId AS BIGINT))
              AND (CAST(:beforeId AS BIGINT) IS NULL OR m.id < CAST(:beforeId AS BIGINT))
            ORDER BY m.id DESC
            LIMIT :limit""".formatted(MESSAGE_FIELDS);


    public static final String FIND_FEED = """
            SELECT %s
            FROM messages m
            JOIN subscriptions s ON s.author_id = m.author_id
            WHERE s.subscriber_id = :subscriberId
              AND m.deleted_at IS NULL
              AND (CAST(:beforeId AS BIGINT) IS NULL OR m.id < CAST(:beforeId AS BIGINT))
            ORDER BY m.id DESC
            LIMIT :limit""".formatted(MESSAGE_FIELDS);

    public static final String UPDATE_MESSAGE = """
            UPDATE messages
            SET text = :text, updated_at = now()
            WHERE id = :id AND deleted_at IS NULL
            RETURNING %s""".formatted(RETURNING_FIELDS);

    public static final String SOFT_DELETE_MESSAGE = """
            UPDATE messages
            SET deleted_at = now(), updated_at = now()
            WHERE id = :id AND deleted_at IS NULL""";

    public static final String SUBSCRIBE = """
            INSERT INTO subscriptions (subscriber_id, author_id)
            VALUES (:subscriberId, :authorId)
            ON CONFLICT (subscriber_id, author_id) DO NOTHING""";

    public static final String UNSUBSCRIBE = """
            DELETE FROM subscriptions
            WHERE subscriber_id = :subscriberId AND author_id = :authorId""";

    public static final String FIND_AUTHORS = """
            SELECT author_id FROM subscriptions
            WHERE subscriber_id = :subscriberId
            ORDER BY author_id""";

    public static final String FIND_SUBSCRIBERS = """
            SELECT subscriber_id FROM subscriptions
            WHERE author_id = :authorId
            ORDER BY subscriber_id""";
}
