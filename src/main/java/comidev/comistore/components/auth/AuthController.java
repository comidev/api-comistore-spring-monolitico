package comidev.comistore.components.auth;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.auth.doc.AuthDoc;
import comidev.comistore.components.auth.request.AuthLogin;
import comidev.comistore.components.role.util.RoleName;
import comidev.comistore.services.jwt.Tokens;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auths")
@AllArgsConstructor
public class AuthController implements AuthDoc {
    private final AuthService authService;

    @PostMapping("/login")
    @ResponseBody
    public Tokens login(@Valid @RequestBody AuthLogin body) {
        return authService.login(body);
    }

    @GetMapping("/t0ken/rut4-v4l7dA-p4r4-sw4gg3r-test/{role}")
    @ResponseBody
    public Tokens tokenGenerate(@PathVariable RoleName role) {
        return authService.tokenGenerate(role);
    }

    @PostMapping("/token/refresh")
    @ResponseBody
    public Tokens tokenRefresh(@RequestHeader(name = "Authorization") String bearerToken) {
        return authService.tokenRefresh(bearerToken);
    }

    @PostMapping("/token/validate")
    public boolean tokenValidate(@RequestHeader(name = "Authorization") String bearerToken) {
        return authService.tokenValidate(bearerToken);
    }
}
