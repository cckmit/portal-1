package ru.protei.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.mail.ReferenceableMimeMessage;
import ru.protei.portal.core.mail.VirtualMailSendChannel;
import ru.protei.portal.tools.notifications.NotificationConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * Фабрика для тестирования уведомлений
 */
@Configuration
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class, NotificationConfiguration.class})
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
            @Override
            public MimeMessage createMailMessage(String messageId, String inReplyTo, List<String> references) {
                return new ReferenceableMimeMessage(Session.getInstance(new Properties()), "test.%id%@smtp.protei.ru", messageId, inReplyTo, references);
            }
        };
    }

}
