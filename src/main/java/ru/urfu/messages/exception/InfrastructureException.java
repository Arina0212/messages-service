package ru.urfu.messages.exception;

import org.springframework.http.HttpStatus;


public class InfrastructureException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public InfrastructureException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }
}
