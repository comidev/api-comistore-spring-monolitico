package comidev.comistore.services;

import static org.junit.jupiter.api.Assertions.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Json {

    private final static Logger LOGGER = LoggerFactory.getLogger(Json.class);

    public String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
            fail("Failed to convert object to json");
            return null;
        }
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(json, clazz);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage());
            fail("Failed to convert json to object");
            return null;
        }
    }
}
