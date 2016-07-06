    package ru.protei.portal.webui.app;

    import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.webui.controller.auth.AuthInterceptor;
import ru.protei.portal.webui.controller.dict.WorkersAPI;
import ru.protei.portal.webui.controller.dict.WorkersAPI_Impl;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

    /**
 * Created by michael on 20.06.16.
 */

@Configuration
@EnableWebMvc
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class})
@ComponentScan(basePackages = "ru.protei.portal.webui.controller")
public class PortalWebConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public WorkersAPI getWorkersController () {
        return new WorkersAPI_Impl();
    }

    @Bean
    public AuthInterceptor getAuthInterceptor () {
        return new AuthInterceptor();
    }

    @Bean(name = "freemarkerConfig")
    public FreeMarkerConfig getFreeMarkerConfig () {
        FreeMarkerConfigurer cfg = new FreeMarkerConfigurer();
        cfg.setDefaultEncoding("utf-8");
        cfg.setTemplateLoaderPath("/WEB-INF/freemarker/");
        return cfg;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable("defaultServletHandler");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/pub/*", "*.css", "*.js", "/login.html")
                ;
    }




    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
            getFreeMarkerConfig();
           registry.freeMarker()
                    .cache(true)
                    .prefix("")
                    .suffix(".ftl")
           ;
    }

        @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
    }
}
