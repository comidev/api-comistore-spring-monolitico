package comidev.comistore.helpers;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.UnsupportedEncodingException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Response {
    private final MockHttpServletResponse response;

    public Response(ResultActions resultActions) {
        this.response = resultActions.andReturn().getResponse();
    }

    public HttpStatus status() throws Exception {
        return HttpStatus.valueOf(this.response.getStatus());
    }

    public <T> T body(Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(bodyString(), clazz);
        } catch (JsonProcessingException e) {
            log.error("Error al deserealizar el JSON -> {}", e.getMessage());
            fail("Failed to convert json to object");
            return null;
        }
    }

    public String bodyString() {
        try {
            return response.getContentAsString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public boolean bodyContains(Object... objects) {
        String body = bodyString();
        return List.of(objects).stream()
                .allMatch(obj -> body.contains(obj.toString()));
    }
}
