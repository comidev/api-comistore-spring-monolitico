package comidev.comistore.components.country;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import comidev.comistore.components.country.doc.CountryDoc;
import comidev.comistore.components.country.response.*;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/countries")
@AllArgsConstructor
public class CountryController implements CountryDoc {

    private final CountryService countryService;

    @GetMapping
    @ResponseBody
    public List<CountryDetails> getAllCountries() {
        return countryService.getAllCountries();
    }
}
