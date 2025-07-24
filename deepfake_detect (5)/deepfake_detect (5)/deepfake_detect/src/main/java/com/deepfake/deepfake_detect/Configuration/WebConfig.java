package com.deepfake.deepfake_detect.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // D:/uploads klasörünü "file:/D:/uploads/" formatında tanımlıyoruz
        // Windows’ta bazen "file:///D:/uploads/" şeklini kullanmak gerekebiliyor.
        String uploadsPath = "file:/D:/uploads/";

        registry
                .addResourceHandler("/uploads/**") // http://localhost:9191/uploads/...
                .addResourceLocations(uploadsPath) // "D:/uploads/" dizininden dosya okuyacak
                .setCachePeriod(0);                // İsterseniz cache devre dışı
    }
}
