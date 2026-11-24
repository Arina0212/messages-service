package ru.urfu.messages.dao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubscriptionRepository {

    Mono<Void> subscribe(long subscriberId, long authorId);

    Mono<Void> unsubscribe(long subscriberId, long authorId);

    Flux<Long> findAuthors(long subscriberId);

    Flux<Long> findSubscribers(long authorId);
}
