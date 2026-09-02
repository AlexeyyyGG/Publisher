package com.cloud.publishing.frontend.config;

import com.cloud.publishing.frontend.security.TokenService;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {
        "com.cloud.publishing.frontend.controller",
        "com.cloud.publishing.frontend.client",
        "com.cloud.publishing.frontend.mapper",
        "com.cloud.publishing.frontend.security"
})
public class WebConfig implements WebMvcConfigurer {
    @Bean("authRestTemplate")
    public RestTemplate authRestTemplate() {
        return new RestTemplate();
    }

    @Bean("apiRestTemplate")
    public RestTemplate apiRestTemplate(TokenService service) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpSession session = Objects.requireNonNull(attrs).getRequest().getSession();
            String token = service.getValidAccessToken(session);
            request.getHeaders().setBearerAuth(token);
            return execution.execute(request, body);
        });
        return restTemplate;
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/articles/js/**")
                .addResourceLocations("/WEB-INF/views/articles/js/");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}