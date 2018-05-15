package park.sharkteam;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowCredentials(true)
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE")
                .allowedOrigins(
                        "https://frontend_site.herokuapp.com",
                        "http://localhost",
                        "http://localhost:3000",
                        "http://127.0.0.1"
                );
    }
}