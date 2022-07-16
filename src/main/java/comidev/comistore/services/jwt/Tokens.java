package comidev.comistore.services.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tokens {
    private String access_token;
    private String refresh_token;
}
