package com.stuparm.project.openapi;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {

        OpenAPI openApi = new OpenAPI();

        // general info component
        openApi.components(new Components()).
                    info(new Info().
                        title("Airport Gate Management System").
                        contact(new Contact().name("Mihailo Stupar"))
                    );




        return openApi;

    }




}
