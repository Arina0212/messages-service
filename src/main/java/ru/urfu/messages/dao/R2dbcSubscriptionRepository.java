package ru.urfu.messages.dao;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public class R2dbcSubscriptionRepository implements SubscriptionRepository {

    private final DatabaseClient databaseClient;

    public R2dbcSubscriptionRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Void> subscribe(long subscriberId, long authorId) {
        return databaseClient.sql(Constants.SUBSCRIBE)
                .bind("subscriberId", subscriberId)
                .bind("authorId", authorId)
                .fetch()
                .rowsUpdated()
                .then();
    }

    @Override
    public Mono<Void> unsubscribe(long subscriberId, long authorId) {
        return databaseClient.sql(Constants.UNSUBSCRIBE)
                .bind("subscriberId", subscriberId)
                .bind("authorId", authorId)
                .fetch()
                .rowsUpdated()
                .then();
    }

    @Override
    public Flux<Long> findAuthors(long subscriberId) {
        return databaseClient.sql(Constants.FIND_AUTHORS)
                .bind("subscriberId", subscriberId)
                .map((row, metadata) -> row.get("author_id", Long.class))
                .all();
    }

    @Override
    public Flux<Long> findSubscribers(long authorId) {
        return databaseClient.sql(Constants.FIND_SUBSCRIBERS)
                .bind("authorId", authorId)
                .map((row, metadata) -> row.get("subscriber_id", Long.class))
                .all();
    }
}
