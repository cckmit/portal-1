package ru.protei.portal.hpsm.config;

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
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.logic.HpsmMainEventHandler;
import ru.protei.portal.hpsm.logic.HpsmPingEventHandler;
import ru.protei.portal.hpsm.service.HpsmService;
import ru.protei.portal.hpsm.service.HpsmServiceImpl;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.utils.EventMsgInputStreamSource;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.JavaMailMessageFactory;
import ru.protei.portal.hpsm.utils.JavaMailSendChannel;

import java.io.IOException;

/**
 * Created by michael on 19.04.17.
 */
@Configuration
public class HpsmConfigurationContext {

    public static final String HPSM_CONFIG_XML_FILE = "hpsm-config.xml";

    @Bean
    public HpsmEnvConfig getHpsmSetup () {

        try {
            return HpsmEnvConfig.load(HPSM_CONFIG_XML_FILE);
        }
        catch (IOException ex) {
            throw new RuntimeException("Unable to load HPSM-configuration, check file " + HPSM_CONFIG_XML_FILE, ex);
        }
//        return new HpsmEnvConfig()
//                .sender("crm_test-user@protei.ru")
//                .receiver("crm_test@protei.ru")
//                .mailServer("smtp.protei.ru", 2525);
    }

    @Bean(name = "hpsmSender")
    public JavaMailSender mailSender () {

        HpsmEnvConfig setup = getHpsmSetup ();

        JavaMailSenderImpl impl = new org.springframework.mail.javamail.JavaMailSenderImpl ();

        impl.setDefaultEncoding(setup.getMailServer().getDefaultCharset());
        impl.setHost(setup.getMailServer().getHost());
        impl.setPort(setup.getMailServer().getPort());

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

//    @Bean
//    public HpsmPingEventHandler getPingCommandHandler () {
//        return new HpsmPingEventHandler();
//    }
//
//    @Bean
//    HpsmMainEventHandler getEventCommandHandler () {
//        return new HpsmMainEventHandler();
//    }


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
