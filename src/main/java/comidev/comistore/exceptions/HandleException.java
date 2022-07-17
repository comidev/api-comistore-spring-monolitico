package comidev.comistore.exceptions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HandleException {

    private final static Logger LOGGER = LoggerFactory.getLogger(HandleException.class);

    // * Error del Cliente
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ErrorMessage> generalError(HttpServletRequest request, HttpException exception) {
        HttpStatus status = exception.getStatus();
        ErrorMessage body = new ErrorMessage(status, exception.getMessage(), request);
        return ResponseEntity.status(status).body(body);
    }

    // * Error del Cliente, Spring o Servidor
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> unexpectedError(HttpServletRequest request, Exception exception) {
        HttpStatus status = statusByException(exception);
        ErrorMessage body = new ErrorMessage(status, exception.getMessage(), request);
        return ResponseEntity.status(status).body(body);
    }

    public static HttpStatus statusByException(Exception exception) {
        String exceptionType = exception.getClass().getSimpleName();

        if (BAD_REQUEST.contains(exceptionType)) {
            return HttpStatus.BAD_REQUEST;
        } else if (UNAUTHORIZED.contains(exceptionType)) {
            return HttpStatus.UNAUTHORIZED;
        } else {
            exception.printStackTrace();
            LOGGER.error("Internal Server Error -> Tipo de Excepcion: {}", exceptionType);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private static final List<String> BAD_REQUEST = List.of(
            "DuplicateKeyException",
            "HttpRequestMethodNotSupportedException",
            "MethodArgumentNotValidException",
            "MissingRequestHeaderException",
            "MissingServletRequestParameterException",
            "MethodArgumentTypeMismatchException",
            "HttpMessageNotReadableException");
    private static final List<String> UNAUTHORIZED = List.of(
            "AccessDeniedException");
}
