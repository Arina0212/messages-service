package ru.urfu.messages.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.urfu.messages.client.UsersClient;
import ru.urfu.messages.dao.MessageRepository;
import ru.urfu.messages.domain.Message;
import ru.urfu.messages.dto.MessagesPage;
import ru.urfu.messages.mapper.MessageMapper;

@Service
public class FeedService {

    private final MessageRepository messages;
    private final UsersClient users;
    private final MessageMapper mapper;

    public FeedService(MessageRepository messages, UsersClient users, MessageMapper mapper) {
        this.messages = messages;
        this.users = users;
        this.mapper = mapper;
    }

    public Mono<MessagesPage> feed(long subscriberId, Long beforeId, int limit, String token) {
        return messages.findFeed(subscriberId, beforeId, limit + 1)
                .collectList()
                .flatMap(found -> {
                    boolean hasMore = found.size() > limit;
                    List<Message> page = hasMore ? found.subList(0, limit) : found;
                    Long next = hasMore && !page.isEmpty() ? page.get(page.size() - 1).getId() : null;
                    Set<Long> authorIds = page.stream().map(Message::getAuthorId).collect(Collectors.toSet());
                    return users.findAuthors(authorIds, token)
                            .map(authors -> new MessagesPage(
                                    page.stream()
                                            .map(message -> mapper.toResponse(message, authors.get(message.getAuthorId())))
                                            .toList(),
                                    next,
                                    limit));
                });
    }
}
