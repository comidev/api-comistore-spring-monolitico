package comidev.comistore.services.swagger;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("comidev")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        final String URL = "https://comidev.vercel.app";
        return new OpenAPI()
                .info(new Info()
                        .title("ComiStore API")
                        .description("API Monolitica con Spring Boot. Puede usar el tokenGenerate del user-controller.")
                        .version("v0.0.1")
                        .contact(new Contact()
                                .name("Omar Miranda")
                                .email("comidev.contacto@gmail.com")
                                .url(URL))
                        .license(new License()
                                .name("comidev")
                                .url(URL)))
                .components(new Components()
                        .addSecuritySchemes("bearer-key", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
