package ru.urfu.messages.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import ru.urfu.messages.config.UsersServiceProperties;
import ru.urfu.messages.dto.UserSummary;

@Component
public class UsersClient {

    private static final Logger log = LoggerFactory.getLogger(UsersClient.class);
    private static final String OK = "OK";

    private final WebClient webClient;

    public UsersClient(WebClient.Builder builder, UsersServiceProperties properties) {
        HttpClient httpClient = HttpClient.create().responseTimeout(properties.getTimeout());
        this.webClient = builder
                .baseUrl(properties.getUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public Mono<UserSummary> findAuthor(long id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .exchangeToMono(response -> {
                    if (response.statusCode().value() == 404) {
                        return Mono.empty();
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(UserEnvelope.class)
                                .flatMap(envelope -> OK.equals(envelope.getCode()) && envelope.getData() != null
                                        ? Mono.just(envelope.getData())
                                        : Mono.empty());
                    }
                    return response.createException().flatMap(Mono::error);
                });
    }

    public Mono<Map<Long, UserSummary>> findAuthors(Collection<Long> ids, String token) {
        if (ids.isEmpty()) {
            return Mono.just(Map.of());
        }
        String csv = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users").queryParam("ids", csv).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(UserListEnvelope.class)
                .map(envelope -> {
                    if (!OK.equals(envelope.getCode()) || envelope.getData() == null) {
                        return Map.<Long, UserSummary>of();
                    }
                    return envelope.getData().getItems().stream()
                            .collect(Collectors.toMap(
                                    UserSummary::getId, Function.identity(), (first, second) -> first));
                })
                .onErrorResume(error -> {
                    log.warn("Не удалось получить имена авторов из Users: {}", error.toString());
                    return Mono.just(Map.of());
                });
    }

    public static class UserEnvelope {

        private String code;
        private String message;
        private UserSummary data;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public UserSummary getData() {
            return data;
        }

        public void setData(UserSummary data) {
            this.data = data;
        }
    }

    public static class UserListEnvelope {

        private String code;
        private String message;
        private ItemsData data;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ItemsData getData() {
            return data;
        }

        public void setData(ItemsData data) {
            this.data = data;
        }
    }

    public static class ItemsData {

        private List<UserSummary> items = List.of();

        public List<UserSummary> getItems() {
            return items;
        }

        public void setItems(List<UserSummary> items) {
            this.items = items == null ? List.of() : items;
        }
    }
}
