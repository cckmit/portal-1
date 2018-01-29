package ru.protei.portal.tools.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * фабрика для бинов, нужных для рассылки уведомлений о записи аудита
 */
@Configuration
public class AuditConfiguration {

    @Bean
    public AuditWriterProcessor getAuditWriterProcessor() {
        return new AuditWriterProcessor();
    }
}
