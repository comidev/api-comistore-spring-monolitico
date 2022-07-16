package comidev.comistore.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class HttpException extends RuntimeException {
    private HttpStatus status;
    private String message;

    public HttpException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
