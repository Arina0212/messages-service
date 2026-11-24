package ru.urfu.messages.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.urfu.messages.dto.ApiResponse;
import ru.urfu.messages.dto.MessagesPage;
import ru.urfu.messages.security.JwtUser;
import ru.urfu.messages.services.FeedService;
import ru.urfu.messages.validators.RequestValidator;

@RestController
public class FeedController {

    private final RequestValidator validator;
    private final FeedService feedService;

    public FeedController(RequestValidator validator, FeedService feedService) {
        this.validator = validator;
        this.feedService = feedService;
    }

    @GetMapping("/messages/feed")
    public Mono<ApiResponse<MessagesPage>> feed(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "before_id", required = false) Long beforeId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return feedService.feed(
                        JwtUser.id(jwt),
                        validator.validateBeforeId(beforeId),
                        validator.validateLimit(limit),
                        JwtUser.token(jwt))
                .map(ApiResponse::ok);
    }
}
