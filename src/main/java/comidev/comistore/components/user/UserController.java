package comidev.comistore.components.user;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.user.dto.Passwords;
import comidev.comistore.components.user.dto.UserReq;
import comidev.comistore.components.user.dto.UserRes;
import comidev.comistore.components.user.dto.Username;
import comidev.comistore.services.jwt.Tokens;
import comidev.comistore.utils.Validator;
import lombok.AllArgsConstructor;
import comidev.comistore.utils.Exists;
import comidev.comistore.utils.Updated;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserRes>> findAll() {
        List<UserRes> users = userService.findAll();

        int status = users.size() == 0 ? 204 : 200;

        return ResponseEntity.status(status).body(users);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserRes saveAdmin(@Valid @RequestBody UserReq userReq, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        UserRes userRes = userService.saveAdmin(userReq);

        return userRes;
    }

    @PostMapping("/username")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Exists existsUsername(@Valid @RequestBody Username username, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        boolean exists = userService.existsUsername(username.getUsername());

        return new Exists(exists);
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Updated updatePassword(@PathVariable Long id,
            @Valid @RequestBody Passwords passwords,
            BindingResult bindingResult) {
        System.out.println("\n\nxdddxdddd\n");
        Validator.checkOrThrowBadRequest(bindingResult);

        boolean updated = userService.updatePassword(id, passwords);

        return new Updated(updated);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Tokens login(@Valid @RequestBody UserReq userReq, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        Tokens tokens = userService.login(userReq);

        return tokens;
    }

    @PostMapping("/token/refresh")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Tokens tokenRefresh(@RequestHeader(name = "Authorization") String bearerToken) {
        Tokens tokens = userService.tokenRefresh(bearerToken);

        return tokens;
    }

    @PostMapping("/token/validate")
    @ResponseStatus(HttpStatus.OK)
    public void tokenValidate(@RequestHeader(name = "Authorization") String bearerToken) {
        userService.tokenValidate(bearerToken);
    }
}
