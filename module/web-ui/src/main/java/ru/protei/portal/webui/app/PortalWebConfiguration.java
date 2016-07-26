package ru.protei.portal.webui.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.tools.migrate.MigrateConfiguration;
import ru.protei.portal.tools.migrate.MigrationRunner;
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
@EnableScheduling
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class, MigrateConfiguration.class})
@ComponentScan(basePackages = "ru.protei.portal.webui.controller")
public class PortalWebConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public MigrationRunner getMigrationRunner () {
        return new MigrationRunner();
    }

    @Bean
    public WorkersAPI getWorkersController() {
        return new WorkersAPI_Impl();
    }

    @Bean
    public AuthInterceptor getAuthInterceptor() {
        return new AuthInterceptor();
    }

    @Bean(name = "freemarkerConfig")
    public FreeMarkerConfig getFreeMarkerConfig() {
        FreeMarkerConfigurer cfg = new FreeMarkerConfigurer();
        cfg.setDefaultEncoding("UTF-8");
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
//        getFreeMarkerConfig();

        FreeMarkerViewResolver htmlResolver = new FreeMarkerViewResolver();
        htmlResolver.setPrefix("");
        htmlResolver.setSuffix(".ftl");
        htmlResolver.setCache(false);   //Set to true during production
        htmlResolver.setContentType("text/html;charset=UTF-8");

        FreeMarkerViewResolver jsResolver = new FreeMarkerViewResolver();
        jsResolver.setPrefix("");
        jsResolver.setSuffix(".js.ftl");
        jsResolver.setCache(false);   //Set to true during production
        jsResolver.setContentType("text/javascript;charset=UTF-8");


        registry.viewResolver(htmlResolver);
        registry.viewResolver(jsResolver);

        //registry.freeMarker().prefix("").suffix(".ftl").cache(false);
//        registry.enableContentNegotiation(false);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("json", MediaType.APPLICATION_JSON);

//        MediaType jsMediaType = new MediaType("text", "javascript", Charset.forName("UTF8"));
//        configurer.mediaType("js", jsMediaType);

//        configurer.ignoreAcceptHeader(true);
//        configurer.favorPathExtension(true);
//            configurer.defaultContentTypeStrategy()
    }
}
