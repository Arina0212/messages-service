package ru.urfu.messages.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.urfu.messages.dto.ApiResponse;
import ru.urfu.messages.dto.CreateMessageRequest;
import ru.urfu.messages.dto.MessageResponse;
import ru.urfu.messages.dto.MessagesPage;
import ru.urfu.messages.dto.UpdateMessageRequest;
import ru.urfu.messages.security.JwtUser;
import ru.urfu.messages.services.MessageService;
import ru.urfu.messages.validators.RequestValidator;

@RestController
public class MessageController {

    private final RequestValidator validator;
    private final MessageService messageService;

    public MessageController(RequestValidator validator, MessageService messageService) {
        this.validator = validator;
        this.messageService = messageService;
    }

    @PostMapping("/messages")
    public Mono<ResponseEntity<ApiResponse<MessageResponse>>> create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody(required = false) CreateMessageRequest request
    ) {
        CreateMessageRequest valid = validator.validateCreate(request);
        return messageService.create(
                        JwtUser.id(jwt),
                        JwtUser.hasRole(jwt, "producer"),
                        valid)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(body)));
    }

    @GetMapping("/messages/{id}")
    public Mono<ApiResponse<MessageResponse>> get(@PathVariable long id) {
        return messageService.get(validator.validateId(id)).map(ApiResponse::ok);
    }

    @PatchMapping("/messages/{id}")
    public Mono<ApiResponse<MessageResponse>> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable long id,
            @RequestBody(required = false) UpdateMessageRequest request
    ) {
        UpdateMessageRequest valid = validator.validateUpdate(request);
        return messageService.update(validator.validateId(id), JwtUser.id(jwt), valid)
                .map(ApiResponse::ok);
    }

    @DeleteMapping("/messages/{id}")
    public Mono<ApiResponse<Void>> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
        return messageService.delete(
                        validator.validateId(id),
                        JwtUser.id(jwt),
                        JwtUser.hasRole(jwt, "admin"))
                .thenReturn(ApiResponse.ok(null));
    }

    @GetMapping("/messages")
    public Mono<ApiResponse<MessagesPage>> page(
            @RequestParam(name = "author_id", required = false) Long authorId,
            @RequestParam(name = "before_id", required = false) Long beforeId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return messageService.page(
                        validator.validateAuthorId(authorId),
                        validator.validateBeforeId(beforeId),
                        validator.validateLimit(limit))
                .map(ApiResponse::ok);
    }
}
