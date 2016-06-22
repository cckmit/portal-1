package ru.protei.portal.webui.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.*;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by michael on 20.06.16.
 */

@Configuration
@EnableWebMvc
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class/*, MainConfiguration.class*/})
@ComponentScan(basePackages = "ru.protei.portal.webui.controller")
public class PortalWebConfiguration extends WebMvcConfigurerAdapter {

//    @Bean
//    public WorkersController getWorkersController () {
//        return new WorkersControllerImpl();
//    }
//
//    @Bean
//    public AuthController getAuthController () {
//        return new AuthControllerImpl();
//    }

//    @Bean
//    public IndexView createIndexView () {
//        return new IndexView();
//    }


    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable("defaultServletHandler");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
    }
}
