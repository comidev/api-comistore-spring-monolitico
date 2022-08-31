package comidev.comistore.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpMethod;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestStats {
    private HttpMethod method;
    private Object body;
    private String authorization;
    private String uri;
    private Map<String, Object> params;

    public RequestStats(HttpMethod method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public void addParam(String key, Object value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
    }

    public String buildUri() {
        if (params != null) {
            uri += uri.contains("?") ? "&" : "?";
            for (Entry<String, Object> items : params.entrySet()) {
                uri += items.getKey() + "=" + items.getValue() + "&";
            }
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }
}
