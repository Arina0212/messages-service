package ru.urfu.messages.services;

import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.urfu.messages.dao.MessageRepository;
import ru.urfu.messages.domain.Message;
import ru.urfu.messages.dto.CreateMessageRequest;
import ru.urfu.messages.dto.MessageResponse;
import ru.urfu.messages.dto.MessagesPage;
import ru.urfu.messages.dto.UpdateMessageRequest;
import ru.urfu.messages.exception.ForbiddenException;
import ru.urfu.messages.exception.ForbiddenException;
import ru.urfu.messages.exception.NotFoundException;
import ru.urfu.messages.mapper.MessageMapper;

@Service
public class MessageService {

    private final MessageRepository messages;
    private final MessageMapper mapper;

    public MessageService(MessageRepository messages, MessageMapper mapper) {
        this.messages = messages;
        this.mapper = mapper;
    }

    public Mono<MessageResponse> create(long authorId, boolean producer, CreateMessageRequest request) {
        if (!producer) {
            return Mono.error(new ForbiddenException("Публиковать сообщения может только producer"));
        }
        return messages.create(authorId, request.getText())
                .map(message -> mapper.toResponse(message, null));
    }

    public Mono<MessageResponse> get(long id) {
        return messages.findById(id)
                .switchIfEmpty(Mono.error(notFound()))
                .map(message -> mapper.toResponse(message, null));
    }

    public Mono<MessagesPage> page(Long authorId, Long beforeId, int limit) {
        return messages.findPage(authorId, beforeId, limit + 1)
                .collectList()
                .map(list -> toPage(list, limit));
    }

    public Mono<MessageResponse> update(long id, long userId, UpdateMessageRequest request) {
        return messages.findById(id)
                .switchIfEmpty(Mono.error(notFound()))
                .flatMap(existing -> {
                    if (!existing.getAuthorId().equals(userId)) {
                        return Mono.error(new ForbiddenException("Редактировать можно только свои сообщения"));
                    }
                    return messages.updateText(id, request.getText());
                })
                .switchIfEmpty(Mono.error(notFound()))
                .map(message -> mapper.toResponse(message, null));
    }

    public Mono<Void> delete(long id, long userId, boolean admin) {
        return messages.findById(id)
                .switchIfEmpty(Mono.error(notFound()))
                .flatMap(existing -> {
                    if (!admin && !existing.getAuthorId().equals(userId)) {
                        return Mono.error(new ForbiddenException("Удалять можно только свои сообщения"));
                    }
                    return messages.softDelete(id);
                })
                .then();
    }

    private MessagesPage toPage(List<Message> found, int limit) {
        boolean hasMore = found.size() > limit;
        List<Message> page = hasMore ? found.subList(0, limit) : found;
        Long next = hasMore && !page.isEmpty() ? page.get(page.size() - 1).getId() : null;
        return new MessagesPage(
                page.stream().map(message -> mapper.toResponse(message, null)).toList(),
                next,
                limit
        );
    }

    private static NotFoundException notFound() {
        return new NotFoundException("MESSAGE_NOT_FOUND", "Сообщение не найдено");
    }
}
