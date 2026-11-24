package ru.urfu.messages.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import ru.urfu.messages.client.UsersClient;
import ru.urfu.messages.dao.SubscriptionRepository;
import ru.urfu.messages.dto.ItemsResponse;
import ru.urfu.messages.exception.NotFoundException;
import ru.urfu.messages.exception.ServiceUnavailableException;
import ru.urfu.messages.exception.ValidationException;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptions;
    private final UsersClient users;

    public SubscriptionService(SubscriptionRepository subscriptions, UsersClient users) {
        this.subscriptions = subscriptions;
        this.users = users;
    }

    public Mono<Void> subscribe(long subscriberId, long authorId) {
        if (subscriberId == authorId) {
            return Mono.error(new ValidationException("SELF_SUBSCRIPTION", "Нельзя подписаться на себя"));
        }
        return users.findAuthor(authorId)
                .switchIfEmpty(Mono.error(new NotFoundException("USER_NOT_FOUND", "Автор не найден")))
                .onErrorMap(WebClientException.class,
                        error -> new ServiceUnavailableException("Сервис Users недоступен, подписка не сохранена"))
                .flatMap(author -> subscriptions.subscribe(subscriberId, authorId));
    }

    public Mono<Void> unsubscribe(long subscriberId, long authorId) {
        return subscriptions.unsubscribe(subscriberId, authorId);
    }

    public Mono<ItemsResponse<Long>> findAuthors(long subscriberId) {
        return subscriptions.findAuthors(subscriberId)
                .collectList()
                .map(ItemsResponse::new);
    }

    public Mono<ItemsResponse<Long>> findSubscribers(long authorId) {
        return subscriptions.findSubscribers(authorId)
                .collectList()
                .map(ItemsResponse::new);
    }
}
