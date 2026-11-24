package ru.urfu.messages.dao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.urfu.messages.domain.Message;


public interface MessageRepository {

    Mono<Message> create(long authorId, String text);

    Mono<Message> findById(long id);

    Flux<Message> findPage(Long authorId, Long beforeId, int limit);

    Flux<Message> findFeed(long subscriberId, Long beforeId, int limit);

    Mono<Message> updateText(long id, String text);

    Mono<Boolean> softDelete(long id);
}
