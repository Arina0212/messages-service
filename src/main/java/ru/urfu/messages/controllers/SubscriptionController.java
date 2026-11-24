package ru.urfu.messages.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.urfu.messages.dto.ApiResponse;
import ru.urfu.messages.dto.ItemsResponse;
import ru.urfu.messages.security.JwtUser;
import ru.urfu.messages.services.SubscriptionService;
import ru.urfu.messages.validators.RequestValidator;

@RestController
public class SubscriptionController {

    private final RequestValidator validator;
    private final SubscriptionService subscriptionService;

    public SubscriptionController(RequestValidator validator, SubscriptionService subscriptionService) {
        this.validator = validator;
        this.subscriptionService = subscriptionService;
    }

    @PutMapping("/subscriptions/{producerId}")
    public Mono<ApiResponse<Void>> subscribe(@AuthenticationPrincipal Jwt jwt, @PathVariable long producerId) {
        return subscriptionService.subscribe(
                        JwtUser.id(jwt),
                        validator.validateId(producerId))
                .thenReturn(ApiResponse.ok(null));
    }

    @DeleteMapping("/subscriptions/{producerId}")
    public Mono<ApiResponse<Void>> unsubscribe(@AuthenticationPrincipal Jwt jwt, @PathVariable long producerId) {
        return subscriptionService.unsubscribe(
                        JwtUser.id(jwt),
                        validator.validateId(producerId))
                .thenReturn(ApiResponse.ok(null));
    }

    @GetMapping("/subscriptions/my")
    public Mono<ApiResponse<ItemsResponse<Long>>> mySubscriptions(@AuthenticationPrincipal Jwt jwt) {
        return subscriptionService.findAuthors(JwtUser.id(jwt)).map(ApiResponse::ok);
    }

    @GetMapping("/subscribers/my")
    public Mono<ApiResponse<ItemsResponse<Long>>> mySubscribers(@AuthenticationPrincipal Jwt jwt) {
        return subscriptionService.findSubscribers(JwtUser.id(jwt)).map(ApiResponse::ok);
    }
}
