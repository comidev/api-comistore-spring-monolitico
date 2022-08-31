package comidev.comistore.helpers;

import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.fail;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Request {
    private final MockMvc mockMvc;
    private RequestStats request;

    public Request(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.request = null;
    }

    public Request get(String uri) {
        return initRequest(HttpMethod.GET, uri);
    }

    public Request post(String uri) {
        return initRequest(HttpMethod.POST, uri);
    }

    public Request put(String uri) {
        return initRequest(HttpMethod.PUT, uri);
    }

    public Request delete(String uri) {
        return initRequest(HttpMethod.DELETE, uri);
    }

    public Request patch(String uri) {
        return initRequest(HttpMethod.PATCH, uri);
    }

    private Request initRequest(HttpMethod method, String uri) {
        if (this.request == null) {
            this.request = new RequestStats(method, uri);
        } else {
            String message = "La peticion ya inició o no se ha cerrado";
            throw new IllegalStateException(message);
        }
        return this;
    }

    public Request body(Object body) {
        if (body != null && request != null) {
            this.request.setBody(body);
        }
        return this;
    }

    public Request authorization(String authorization) {
        if (authorization != null && request != null) {
            this.request.setAuthorization(authorization);
        }
        return this;
    }

    public Request addParam(String key, Object value) {
        if (key != null && value != null && request != null) {
            this.request.addParam(key, value);
        }
        return this;
    }

    public Response send() throws Exception {
        if (this.request == null) {
            String message = "Debe iniciar con algun metodo http";
            throw new IllegalArgumentException(message);
        }

        MockHttpServletRequestBuilder build = MockMvcRequestBuilders.request(
                request.getMethod(), request.buildUri());

        Object body = request.getBody();
        if (body != null) {
            build = build.content(toJson(body))
                    .contentType(MediaType.APPLICATION_JSON);
        }

        String authorization = request.getAuthorization();
        if (authorization != null) {
            build = build.header("Authorization", authorization);
        }

        Response response = new Response(mockMvc.perform(build));
        this.request = null;
        return response;
    }

    private String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            fail("Failed to convert object to json");
            return null;
        }
    }
}
