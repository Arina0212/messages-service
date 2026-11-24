package ru.urfu.messages.mapper;

import org.springframework.stereotype.Component;
import ru.urfu.messages.domain.Message;
import ru.urfu.messages.dto.MessageResponse;
import ru.urfu.messages.dto.UserSummary;

@Component
public class MessageMapper {

    public MessageResponse toResponse(Message message, UserSummary author) {
        return new MessageResponse(
                message.getId(),
                message.getAuthorId(),
                message.getText(),
                author,
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
