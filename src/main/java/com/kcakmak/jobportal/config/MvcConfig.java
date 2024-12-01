package com.kcakmak.jobportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/*
    When files (like images) are uploaded to a specific directory on the server,
    they are not automatically accessible as static resources via HTTP.
    By default, Spring Boot serves static resources from specific locations, such as src/main/resources/static.
    If the uploaded files are stored outside these default locations,
    the application needs to explicitly tell Spring MVC to expose this directory
    so it can serve files as static resources.

    This configuration class achieves that by mapping the directory
    containing the uploaded files to a URL path (/photos/**)
    so that users or the application itself can access them through HTTP.
*/

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = "photos";

    // Override the default implementation to set up a custom resource handler
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory(UPLOAD_DIR, registry);
    }

    // To support for any request that comme in for our upload directory,
    // this method converts the uploadDir string to a Path.
    // This maps the web URL request starting with "/photos/**" to a file system location
    // for example: "file:<absolute path to photos directory>"
    private void exposeDirectory(String uploadDir, ResourceHandlerRegistry registry) {
        Path path = Paths.get(uploadDir);
        registry.addResourceHandler("/" + uploadDir + "/**")
                .addResourceLocations("file:" + path.toAbsolutePath() + "/");
    }
}
