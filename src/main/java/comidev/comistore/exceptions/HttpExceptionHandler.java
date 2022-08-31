package comidev.comistore.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class HttpExceptionHandler {
    // * ERROR DE VALIDACION
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> validationException(HttpServletRequest request,
            MethodArgumentNotValidException exception) {
        ErrorMessage body = new ErrorMessage(HttpStatus.BAD_REQUEST,
                extractedMessage(exception.getBindingResult().getFieldErrors()),
                request);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    private final String extractedMessage(List<FieldError> errors) {
        return "[{ " + errors.stream()
                .map(e -> "'" + e.getField() + "' : '"
                        + e.getDefaultMessage() + "'")
                .collect(Collectors.joining(" }, { "))
                + " }]";
    }

    // * Error del Cliente
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ErrorMessage> generalError(
            HttpServletRequest request, HttpException exception) {
        ErrorMessage body = new ErrorMessage(exception.getStatus(),
                exception.getMessage(), request);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    // * Error del Servidor
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> unexpectedError(
            HttpServletRequest request, Exception exception) {
        ErrorMessage body = createMessage(request, exception);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    public static ErrorMessage createMessage(
            HttpServletRequest request, Exception exception) {
        String exceptionType = exception.getClass().getSimpleName();
        log.error("Tipo de Excepción -> {}", exceptionType);

        HttpStatus status;
        String message = exception.getMessage();

        if (BAD_REQUEST.contains(exceptionType)) {
            status = HttpStatus.BAD_REQUEST;
            if ("MethodArgumentTypeMismatchException".equals(exceptionType)) {
                message = errorOfTypes(message);
            }
            if ("HttpMessageNotReadableException".equals(exceptionType)) {
                message = errorOfDesarialize(message);
            }
        } else if (UNAUTHORIZED.contains(exceptionType)) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (CONFLICT.contains(exceptionType)) {
            status = HttpStatus.CONFLICT;
            if ("DataIntegrityViolationException".equals(exceptionType)) {
                message = errorUniqueColumn(message);
            }
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            exception.printStackTrace();
            log.error("Message -> {}", message);
        }
        return new ErrorMessage(status, message, request);
    }

    private static String errorOfTypes(String message) {
        if (message.contains("enum")) {
            String[] fields = message.split("\\.");
            int length = fields.length;
            message = buildErrorType(fields[length - 1], "enum", fields[length - 2]);
        } else if (message.contains("IllegalArgumentException: Invalid boolean")) {
            String[] fields = message.split(" ");
            String valor = fields[fields.length - 1];
            String simpleValor = valor.substring(1, valor.length() - 1);
            message = buildErrorType(simpleValor, "boolean", "(true o false)");
        } else if (message.contains("NumberFormatException")) {
            String[] fields = message.split(" ");
            String valor = fields[fields.length - 1];
            String simpleValor = valor.substring(1, valor.length() - 1);
            if (message.contains("'int'")) {
                message = buildErrorType(simpleValor, "numero", "(1, 2, 3, 4)");
            } else {
                message = buildErrorType(simpleValor, "numero", "(1.1, 20.154)");
            }
        }
        return message;
    }

    private static String errorOfDesarialize(String message) {
        if (message.contains("Enum class")) {
            String messageTotal = message.split(";")[0];
            String[] nameAndValue = messageTotal.split(" from String ");
            String value = nameAndValue[1].split(":")[0];
            value = value.substring(1, value.length() - 1);
            String[] fields3 = nameAndValue[0].split("\\.");
            int length3 = fields3.length;
            String name = fields3[length3 - 1];
            name = name.substring(0, name.length() - 1);
            return buildErrorType(value, "enum", name);
        }
        return message;
    }

    private static String buildErrorType(String value, String type, String example) {
        return "El (valor=" + value + ") no es de tipo " + type + " -> " + example;
    }

    private static String errorUniqueColumn(String message) {
        String field = message.split("\\(")[1].split(" ")[0];
        return "El campo '" + field + "' ya existe y éste debe ser único :D!";
    }

    public static HttpStatus statusByException(Exception exception) {
        String exceptionType = exception.getClass().getSimpleName();
        if (BAD_REQUEST.contains(exceptionType)) {
            return HttpStatus.BAD_REQUEST;
        } else if (UNAUTHORIZED.contains(exceptionType)) {
            return HttpStatus.UNAUTHORIZED;
        } else if (CONFLICT.contains(exceptionType)) {
            return HttpStatus.CONFLICT;
        } else {
            exception.printStackTrace();
            log.error("Internal Server Error -> Tipo de Excepcion: {}", exceptionType);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private static final List<String> CONFLICT = List.of(
            "DataIntegrityViolationException");

    private static final List<String> BAD_REQUEST = List.of(
            "DuplicateKeyException",
            "HttpRequestMethodNotSupportedException",
            "MissingRequestHeaderException",
            "MissingServletRequestParameterException",
            "MethodArgumentTypeMismatchException",
            "HttpMessageNotReadableException");
    private static final List<String> UNAUTHORIZED = List.of(
            "AccessDeniedException",
            "InternalAuthenticationServiceException");
}
