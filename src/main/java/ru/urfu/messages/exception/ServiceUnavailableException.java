package ru.urfu.messages.exception;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends InfrastructureException {

    public ServiceUnavailableException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, "USERS_UNAVAILABLE", message);
    }
}
