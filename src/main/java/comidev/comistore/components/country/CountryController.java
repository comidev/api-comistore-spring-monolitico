package comidev.comistore.components.country;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.country.dto.CountryRes;

import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/countries")
@AllArgsConstructor
public class CountryController {

    private final CountryRepo countryRepo;

    @Operation(summary = "findAll - Devuelve lista de paises", responses = {
            @ApiResponse(description = "OK", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountryRes.class))),
            @ApiResponse(responseCode = "204", description = "NO CONTENT - No hay paises", content = @Content),
    })
    @GetMapping
    public ResponseEntity<List<CountryRes>> findAll() {
        List<Country> countriesDB = countryRepo.findAll();

        List<CountryRes> countriesRes = countriesDB.stream()
                .map(CountryRes::new)
                .toList();

        return ResponseEntity
                .status(countriesRes.isEmpty() ? 204 : 200)
                .body(countriesRes);
    }
}
