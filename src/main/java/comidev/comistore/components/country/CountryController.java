package comidev.comistore.components.country;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.country.dto.CountryRes;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/countries")
@AllArgsConstructor
public class CountryController {

    private final CountryRepo countryRepo;

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
