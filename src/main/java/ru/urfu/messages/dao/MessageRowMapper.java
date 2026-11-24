package ru.urfu.messages.dao;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.BiFunction;
import org.springframework.stereotype.Component;
import ru.urfu.messages.domain.Message;

@Component
public class MessageRowMapper implements BiFunction<Row, RowMetadata, Message> {

    @Override
    public Message apply(Row row, RowMetadata metadata) {
        return new Message(
                row.get("id", Long.class),
                row.get("author_id", Long.class),
                row.get("text", String.class),
                toInstant(row.get("created_at")),
                toInstant(row.get("updated_at"))
        );
    }

    private static Instant toInstant(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime.toInstant();
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.toInstant(ZoneOffset.UTC);
        }
        throw new IllegalStateException("Неожиданный тип времени: " + value.getClass().getName());
    }
}
