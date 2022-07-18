package comidev.comistore.services.validator;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import comidev.comistore.exceptions.HttpException;

public class Validator {
    private static String from(List<FieldError> errors) {
        String message = "";
        int indice = 1;
        for (FieldError err : errors) {
            String field = err.getField();
            String fieldMsg = err.getDefaultMessage();
            message += " | (" + indice + ") " + field + ": " + fieldMsg;
            indice++;
        }
        return message;
    }

    public static void checkOrThrowBadRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = Validator.from(bindingResult.getFieldErrors());
            throw new HttpException(BAD_REQUEST, message);
        }
    }
}
