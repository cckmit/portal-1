package ru.protei.portal.hpsm;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import ru.protei.portal.hpsm.struct.EventMsg;
import ru.protei.portal.hpsm.struct.HpsmSetup;

/**
 * Created by michael on 19.04.17.
 */
@Configuration
public class HpsmConfiguration {

    @Bean(name = "hpsmSender")
    public JavaMailSender mailSender () {
        JavaMailSenderImpl impl = new org.springframework.mail.javamail.JavaMailSenderImpl ();

        impl.setDefaultEncoding("utf-8");
        impl.setHost("smtp.protei.ru");
        impl.setPort(2525);


        return impl;
    }

    @Bean
    public HpsmSetup getHpsmSetup () {
        HpsmSetup setup = new HpsmSetup();
        setup.senderAddress = "crm_test-user@protei.ru";
        setup.hpsmAddress = "crm_test@protei.ru";
        return setup;
    }


    @Bean
    //@InboundChannelAdapter(value = "testReceiveEmailChannel", poller = @Poller(fixedDelay = "5000", taskExecutor = "asyncTaskExecutor"))
    public MessageSource mailMessageSource() {
        MailReceivingMessageSource mailReceivingMessageSource = new MailReceivingMessageSource(imapMailReceiver());
        // other setters here

        return mailReceivingMessageSource;
    }

    @Bean
    public MailReceiver imapMailReceiver() {
        ImapMailReceiver imapMailReceiver = new ImapMailReceiver("imaps://crm_test-user:hae7Eito@imap.protei.ru:993/INBOX");
        imapMailReceiver.setShouldMarkMessagesAsRead(false);
        imapMailReceiver.setShouldDeleteMessages(false);
        imapMailReceiver.setEmbeddedPartsAsBytes(false);

        return imapMailReceiver;
    }


    @Bean
    public XStream xstreamSerializer () {
        XStream x = new XStream(new Xpp3Driver(new XmlFriendlyNameCoder("_-", "_")));

        x.autodetectAnnotations(true);
        x.processAnnotations(EventMsg.class);
        return x;
    }


    @Bean
    @Scope("prototype")
    public EventMsgInputStreamSource eventMsgInputStreamSource () {
        return new EventMsgInputStreamSource ();
    }




}
