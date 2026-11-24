package ru.urfu.messages.dao;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.urfu.messages.domain.Message;

@Repository
public class R2dbcMessageRepository implements MessageRepository {

    private final DatabaseClient databaseClient;
    private final MessageRowMapper rowMapper;

    public R2dbcMessageRepository(DatabaseClient databaseClient, MessageRowMapper rowMapper) {
        this.databaseClient = databaseClient;
        this.rowMapper = rowMapper;
    }

    @Override
    public Mono<Message> create(long authorId, String text) {
        return databaseClient.sql(Constants.INSERT_MESSAGE)
                .bind("authorId", authorId)
                .bind("text", text)
                .map(rowMapper)
                .one();
    }

    @Override
    public Mono<Message> findById(long id) {
        return databaseClient.sql(Constants.FIND_MESSAGE_BY_ID)
                .bind("id", id)
                .map(rowMapper)
                .one();
    }

    @Override
    public Flux<Message> findPage(Long authorId, Long beforeId, int limit) {
        GenericExecuteSpec spec = databaseClient.sql(Constants.FIND_MESSAGES)
                .bind("limit", limit);
        spec = authorId == null
                ? spec.bindNull("authorId", Long.class)
                : spec.bind("authorId", authorId);
        spec = beforeId == null
                ? spec.bindNull("beforeId", Long.class)
                : spec.bind("beforeId", beforeId);
        return spec.map(rowMapper).all();
    }

    @Override
    public Flux<Message> findFeed(long subscriberId, Long beforeId, int limit) {
        GenericExecuteSpec spec = databaseClient.sql(Constants.FIND_FEED)
                .bind("subscriberId", subscriberId)
                .bind("limit", limit);
        spec = beforeId == null
                ? spec.bindNull("beforeId", Long.class)
                : spec.bind("beforeId", beforeId);
        return spec.map(rowMapper).all();
    }

    @Override
    public Mono<Message> updateText(long id, String text) {
        return databaseClient.sql(Constants.UPDATE_MESSAGE)
                .bind("id", id)
                .bind("text", text)
                .map(rowMapper)
                .one();
    }

    @Override
    public Mono<Boolean> softDelete(long id) {
        return databaseClient.sql(Constants.SOFT_DELETE_MESSAGE)
                .bind("id", id)
                .fetch()
                .rowsUpdated()
                .map(updated -> updated > 0);
    }
}
