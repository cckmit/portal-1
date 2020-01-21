package ru.protei.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.protei.portal.util.ProductFieldConverter;

@Configuration
public class ApiConfiguration  extends WebMvcConfigurerAdapter {

    @Override
    public void addFormatters( FormatterRegistry registry) {
        registry.addConverter(new ProductFieldConverter());
    }
}
