package ru.protei.portal.test.notification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.mail.VirtualMailSendChannel;
import ru.protei.portal.test.event.EventHandlerRegistry;
import ru.protei.portal.tools.notifications.NotificationConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.mail.internet.MimeMessage;

/**
 * Фабрика для тестирования уведомлений
 */
@Configuration
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class, NotificationConfiguration.class})
public class TestNotificationConfiguration {

    @Bean
    public MailSendChannel getMailChannel() {
        return new VirtualMailSendChannel();
    }

    @Bean
    public MailMessageFactory getMailMessageFactory() {
        return new MailMessageFactory() {
            @Override
            public MimeMessage createMailMessage() {
                return new JavaMailSenderImpl().createMimeMessage();
            }
        };
    }

}
