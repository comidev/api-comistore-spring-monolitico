package comidev.comistore.components.user;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.user.doc.UserDoc;
import comidev.comistore.components.user.request.UserCreate;
import comidev.comistore.components.user.response.UserDetails;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController implements UserDoc {
    private final UserService userService;

    @GetMapping
    @ResponseBody
    public List<UserDetails> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserDetails registerUserAdmin(@Valid @RequestBody UserCreate body) {
        return userService.registerUserAdmin(body);
    }

    @GetMapping("/exists")
    public boolean existsUsername(
            @RequestParam(name = "username") String username) {
        return userService.existsUsername(username);
    }
}
