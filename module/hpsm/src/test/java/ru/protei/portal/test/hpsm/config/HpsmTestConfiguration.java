package ru.protei.portal.test.hpsm.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
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
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.utils.EventMsgInputStreamSource;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.JavaMailMessageFactory;
import ru.protei.portal.hpsm.utils.JavaMailSendChannel;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by michael on 19.04.17.
 */
@Configuration
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class})
public class HpsmTestConfiguration {

    @Bean
    public HpsmEnvConfig getHpsmSetup () {
        return new HpsmEnvConfig()
                .sender("crm_test-user@protei.ru")
                .receiver("crm_test@protei.ru")
                .mailServer("smtp.protei.ru", 2525);
    }

    @Bean(name = "hpsmSender")
    public JavaMailSender mailSender () {

        HpsmEnvConfig setup = getHpsmSetup ();

        JavaMailSenderImpl impl = new JavaMailSenderImpl ();

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


    @Bean
    public HpsmTestUtils createTestUtils () {
        return new HpsmTestUtils ();
    }

}
