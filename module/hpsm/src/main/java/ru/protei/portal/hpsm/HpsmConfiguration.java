package ru.protei.portal.hpsm;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.handler.HpsmMainEventHandler;
import ru.protei.portal.hpsm.handler.HpsmPingEventHandler;
import ru.protei.portal.hpsm.service.HpsmService;
import ru.protei.portal.hpsm.service.HpsmServiceImpl;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmSetup;
import ru.protei.portal.hpsm.utils.EventMsgInputStreamSource;
import ru.protei.portal.hpsm.utils.JavaMailMessageFactory;
import ru.protei.portal.hpsm.utils.JavaMailSendChannel;

/**
 * Created by michael on 19.04.17.
 */
@Configuration
@Import(MainConfiguration.class)
public class HpsmConfiguration {

    @Bean
    public HpsmSetup getHpsmSetup () {
        return new HpsmSetup()
                .sender("crm_test-user@protei.ru")
                .receiver("crm_test@protei.ru")
                .mailServer("smtp.protei.ru", 2525);
    }

    @Bean(name = "hpsmSender")
    public JavaMailSender mailSender () {

        HpsmSetup setup = getHpsmSetup ();

        JavaMailSenderImpl impl = new org.springframework.mail.javamail.JavaMailSenderImpl ();

        impl.setDefaultEncoding("utf-8");
        impl.setHost(setup.getMailServerHost());
        impl.setPort(setup.getMailServerPort());

        return impl;
    }

    @Bean
    public HpsmService getHpsmService () {
        return new HpsmServiceImpl();
    }

    @Bean(name = "hpsmSendChannel")
    public MailSendChannel getRealMailSendChannel () {
        return new JavaMailSendChannel(mailSender());
    }

    @Bean(name = "hpsmMessageFactory")
    public MailMessageFactory getMailMessageFactory () {
        return new JavaMailMessageFactory (mailSender());
    }

    @Bean
    public HpsmPingEventHandler getPingCommandHandler () {
        return new HpsmPingEventHandler();
    }

    @Bean
    HpsmMainEventHandler getEventCommandHandler () {
        return new HpsmMainEventHandler();
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


    @Bean(name = "hpsmSerializer")
    public XStream xstreamSerializer () {
        XStream x = new XStream(new Xpp3Driver(new XmlFriendlyNameCoder("_-", "_")));
        x.autodetectAnnotations(true);
        x.processAnnotations(HpsmMessage.class);
        return x;
    }


    @Bean
    @Scope("prototype")
    public EventMsgInputStreamSource eventMsgInputStreamSource () {
        return new EventMsgInputStreamSource();
    }




}
