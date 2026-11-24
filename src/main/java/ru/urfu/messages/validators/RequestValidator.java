package ru.urfu.messages.validators;

import org.springframework.stereotype.Component;
import ru.urfu.messages.dto.CreateMessageRequest;
import ru.urfu.messages.dto.UpdateMessageRequest;
import ru.urfu.messages.exception.ValidationException;

@Component
public class RequestValidator {

    private static final int MAX_TEXT = 5000;
    private static final int MAX_LIMIT = 100;

    public CreateMessageRequest validateCreate(CreateMessageRequest request) {
        if (request == null) {
            throw new ValidationException("Тело запроса обязательно");
        }
        return new CreateMessageRequest(text(request.getText()));
    }

    public UpdateMessageRequest validateUpdate(UpdateMessageRequest request) {
        if (request == null) {
            throw new ValidationException("Тело запроса обязательно");
        }
        return new UpdateMessageRequest(text(request.getText()));
    }

    public long validateId(long id) {
        if (id < 1) {
            throw new ValidationException("id должен быть положительным");
        }
        return id;
    }

    public int validateLimit(int limit) {
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new ValidationException("limit должен быть в диапазоне 1.." + MAX_LIMIT);
        }
        return limit;
    }

    public Long validateBeforeId(Long beforeId) {
        if (beforeId == null) {
            return null;
        }
        if (beforeId < 1) {
            throw new ValidationException("before_id должен быть положительным");
        }
        return beforeId;
    }

    public Long validateAuthorId(Long authorId) {
        if (authorId == null) {
            return null;
        }
        if (authorId < 1) {
            throw new ValidationException("author_id должен быть положительным");
        }
        return authorId;
    }

    private String text(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException("EMPTY_TEXT", "Текст сообщения не может быть пустым");
        }
        if (trimmed.length() > MAX_TEXT) {
            throw new ValidationException("TEXT_TOO_LONG", "Текст длиннее " + MAX_TEXT + " символов");
        }
        return trimmed;
    }
}
