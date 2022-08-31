package comidev.comistore.components.country;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import comidev.comistore.config.ApiIntegrationTest;
import comidev.comistore.helpers.Fabric;

@ApiIntegrationTest
public class CountryControllerTest {
    @Autowired
    private Fabric fabric;
    @Autowired
    private MockMvc mockMvc;

    // * GET, /categories
    @Test
    void OK_CuandoHayAlMenosHayUnaCategoria_findAll() throws Exception {
        fabric.createCountry(null);

        ResultActions res = mockMvc.perform(get("/countries"));

        res.andExpect(status().isOk());
    }
}
