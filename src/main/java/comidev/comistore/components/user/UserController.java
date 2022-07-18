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

import comidev.comistore.components.user.dto.Exists;
import comidev.comistore.components.user.dto.Passwords;
import comidev.comistore.components.user.dto.Updated;
import comidev.comistore.components.user.dto.UserReq;
import comidev.comistore.components.user.dto.UserRes;
import comidev.comistore.components.user.dto.Username;
import comidev.comistore.services.jwt.Tokens;
import comidev.comistore.services.validator.Validator;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "tokenGenerate - Genera tokens por Rol, solo para probar en Swagger", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tokens.class))),
    })
    @GetMapping("/t0ken/rut4-v4l7dA-p4r4-sw4gg3r-test/{roleName}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Tokens tokenGenerate(@PathVariable(name = "roleName") String role) {
        return userService.tokenGenerate(role);
    }

    @Operation(summary = "findAll - Devuelve lista de usuarios", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRes.class))),
            @ApiResponse(responseCode = "204", description = "NO CONTENT - No hay usuarios", content = @Content),
    })
    @GetMapping
    public ResponseEntity<List<UserRes>> findAll() {
        List<UserRes> users = userService.findAll();

        int status = users.size() == 0 ? 204 : 200;

        return ResponseEntity.status(status).body(users);
    }

    @Operation(summary = "saveAdmin - Registra un usuario admin", responses = {
            @ApiResponse(responseCode = "201", description = "CREATED - Se registra el usuario admin", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRes.class))),
            @ApiResponse(responseCode = "409", description = "CONFLICT - El username ya existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST - Error de validacion", content = @Content),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserRes saveAdmin(@Valid @RequestBody UserReq userReq, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        UserRes userRes = userService.saveAdmin(userReq);

        return userRes;
    }

    @Operation(summary = "existsUsername - Verifica si el username se encuentra registrado", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Exists.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST - Error de validacion", content = @Content),
    })
    @PostMapping("/username")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Exists existsUsername(@Valid @RequestBody Username username, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        boolean exists = userService.existsUsername(username.getUsername());

        return new Exists(exists);
    }

    @Operation(summary = "updatePassword - Actualiza el password por id", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Updated.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST - Error de validacion", content = @Content),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - El id no existe", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Password Incorrecto | Se requiere Token con Rol(es): CLIENTE", content = @Content),
    }, security = @SecurityRequirement(name = "bearer-key"))
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Updated updatePassword(@PathVariable Long id,
            @Valid @RequestBody Passwords passwords,
            BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        boolean updated = userService.updatePassword(id, passwords);

        return new Updated(updated);
    }

    @Operation(summary = "login - Devuelve tokens por username y password", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tokens.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST - Error de validacion", content = @Content),
            @ApiResponse(responseCode = "404", description = "NOT FOUND - El id no existe", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Username y/o Password incorrecto", content = @Content),
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Tokens login(@Valid @RequestBody UserReq userReq, BindingResult bindingResult) {
        Validator.checkOrThrowBadRequest(bindingResult);

        Tokens tokens = userService.login(userReq);

        return tokens;
    }

    @Operation(summary = "tokenRefresh - Devuelve tokens por Token Refresh", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tokens.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Token invalido", content = @Content),
    })
    @PostMapping("/token/refresh")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Tokens tokenRefresh(@RequestHeader(name = "Authorization") String bearerToken) {
        Tokens tokens = userService.tokenRefresh(bearerToken);

        return tokens;
    }

    @Operation(summary = "tokenValidate - Verifica si el token es valido", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Token invalido", content = @Content),
    })
    @PostMapping("/token/validate")
    @ResponseStatus(HttpStatus.OK)
    public void tokenValidate(@RequestHeader(name = "Authorization") String bearerToken) {
        userService.tokenValidate(bearerToken);
    }
}
