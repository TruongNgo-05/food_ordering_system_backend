package com.example.project_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// upload anh
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // upload image
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // qr code
        registry.addResourceHandler("/qrcodes/**")
                .addResourceLocations("file:uploads/qrcodes/");
    }
}