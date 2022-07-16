package comidev.comistore.services.jwt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Payload {
    private Long id;
    private String username;
    private List<String> roles;

    @SuppressWarnings("unchecked")
    public Payload(Map<String, Object> payloadMap) {

        this.id = ((Integer) payloadMap.get("id")).longValue();
        this.username = (String) payloadMap.get("username");
        this.roles = (List<String>) payloadMap.get("roles");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> payloadMap = new HashMap<>();

        payloadMap.put("id", id);
        payloadMap.put("username", username);
        payloadMap.put("roles", roles);

        return payloadMap;
    }
}
