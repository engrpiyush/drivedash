package com.drivemond.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC configuration: CORS policy and static resource mappings.
 *
 * <p>CORS is intentionally permissive in development; tighten
 * {@code allowedOriginPatterns} per environment via {@code application.yml}.
 */
@Configuration
@Controller
public class WebConfig implements WebMvcConfigurer {

    /** Silences the NoResourceFoundException browsers trigger with automatic favicon requests. */
    @GetMapping("/favicon.ico")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void favicon() {}

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files from the configured upload directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // Standard static assets (Swagger UI, Webjars)
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
