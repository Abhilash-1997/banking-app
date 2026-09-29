package com.novabank.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfiguration {

    @Bean
    OpenAPI novaBankOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("NovaBank API")
                        .version("v1")
                        .description("Modular monolith banking API. Increment 1: health + /api/v1/me."))
                .components(new Components().addSecuritySchemes("bearer-jwt",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
