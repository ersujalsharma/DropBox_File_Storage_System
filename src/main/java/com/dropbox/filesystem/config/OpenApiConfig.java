package com.dropbox.filesystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fileStorageOpenApi() {
        return new OpenAPI().info(new Info()
                .title("DropBox File Storage System API")
                .description("MVP APIs for upload sessions, file metadata, download links, and sharing")
                .version("v1")
                .contact(new Contact().name("DropBox File Storage System").url("https://github.com/"))
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
        );
    }
}
