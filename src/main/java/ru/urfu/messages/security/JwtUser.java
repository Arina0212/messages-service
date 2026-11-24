package ru.urfu.messages.security;

import org.springframework.security.oauth2.jwt.Jwt;
import ru.urfu.messages.exception.ForbiddenException;

public final class JwtUser {

    private JwtUser() {
    }

    public static long id(Jwt jwt) {
        try {
            return Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException | NullPointerException exception) {
            throw new ForbiddenException("В токене нет корректного идентификатора пользователя");
        }
    }

    public static boolean hasRole(Jwt jwt, String role) {
        return jwt.getClaimAsStringList("roles") != null
                && jwt.getClaimAsStringList("roles").contains(role);
    }

    public static String token(Jwt jwt) {
        return jwt.getTokenValue();
    }
}
