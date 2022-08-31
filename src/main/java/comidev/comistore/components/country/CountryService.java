package comidev.comistore.components.country;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import comidev.comistore.components.country.request.CountryCreate;
import comidev.comistore.components.country.response.CountryDetails;
import comidev.comistore.exceptions.HttpException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CountryService {
    private final CountryRepo repo;

    public List<CountryDetails> getAllCountries() {
        return repo.findAll().stream()
                .map(CountryDetails::new)
                .collect(Collectors.toList());
    }

    public CountryDetails registerCountry(CountryCreate body) {
        Country create = new Country(body);
        return new CountryDetails(repo.save(create));
    }

    public Country findCountryByName(String name) {
        return repo.findByName(name).orElseThrow(() -> {
            String message = "El pais no existe!";
            return new HttpException(HttpStatus.NOT_FOUND, message);
        });
    }
}
