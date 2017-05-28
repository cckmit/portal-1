package ru.protei.portal.test.hpsm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;

/**
 * Created by michael on 16.05.17.
 */
@Configuration
public class RawMailTestConfig {

    @Bean
    public ImapMailReceiver imapMailReceiver() {

        ImapMailReceiver imapMailReceiver = new ImapMailReceiver("imaps://crm_test-user:hae7Eito@imap.protei.ru:993/INBOX");

        imapMailReceiver.setShouldMarkMessagesAsRead(true);
        imapMailReceiver.setShouldDeleteMessages(true);
        imapMailReceiver.setEmbeddedPartsAsBytes(false);
//        imapMailReceiver.setUserFlag(null);

        return imapMailReceiver;
    }


    @Bean(name = "rawTestSource")
    public MessageSource<Object> getMessageSource () {
        return new MailReceivingMessageSource(imapMailReceiver());
    }
}
