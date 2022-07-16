package comidev.comistore.exceptions;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErrorMessage {
    private String error;
    private int status;
    private String message;
    private String method;
    private String path;
    private String timestamp;

    public ErrorMessage(HttpStatus status, String message, HttpServletRequest request) {
        this.error = status.getReasonPhrase().toUpperCase();
        this.status = status.value();
        this.message = message;
        this.method = request.getMethod();
        this.path = request.getRequestURI();
        this.timestamp = new Timestamp(System.currentTimeMillis()).toString();
    }
}
