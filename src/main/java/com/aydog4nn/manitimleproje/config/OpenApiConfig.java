package com.aydog4nn.manitimleproje.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI duoSpaceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("DuoSpace API")
                        .version("v1")
                        .description("DuoSpace projesindeki kullanıcı, oda ve ortak liste endpointlerini buradan deneyebilirsin. Korunan endpointlerde önce giriş yapıp gelen tokeni Authorize kısmına yazman gerekir."))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
