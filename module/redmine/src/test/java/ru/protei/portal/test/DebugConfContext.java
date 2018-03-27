package ru.protei.portal.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.protei.portal.core.controller.cloud.FileController;

@Configuration
public class DebugConfContext {
    @Bean
    public FileController getFileController () {
         return new FileController();
    }
}
