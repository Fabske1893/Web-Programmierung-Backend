package com.rezeptapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = "com.rezeptapp")
public class Application implements WebMvcConfigurer{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

     @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
}}
