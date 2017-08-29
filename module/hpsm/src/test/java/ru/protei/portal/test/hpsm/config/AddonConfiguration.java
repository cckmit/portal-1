package ru.protei.portal.test.hpsm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.protei.portal.core.controller.cloud.FileController;

@Configuration
public class AddonConfiguration {

    @Bean
    public FileController getFileController () {
        return new FileController();
    }
}
