package ru.protei.portal.hpsm.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.logic.InboundMainMessageHandler;
import ru.protei.portal.hpsm.logic.InboundPingMessageHandler;
import ru.protei.portal.hpsm.logic.BackChannelHandlerFactory;
import ru.protei.portal.hpsm.logic.BackChannelHandlerFactoryImpl;
import ru.protei.portal.hpsm.service.HpsmMessageFactoryImpl;
import ru.protei.portal.hpsm.service.HpsmService;
import ru.protei.portal.hpsm.service.HpsmServiceImpl;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.utils.*;

import java.io.IOException;

/**
 * Created by michael on 19.04.17.
 */
@Configuration
public class HpsmConfigurationContext {

    public static final String HPSM_CONFIG_XML_FILE = "hpsm-config.xml";


    @Bean(name = "hpsmSerializer")
    public XStream xstreamSerializer () {
        XStream x = new XStream(new Xpp3Driver(new XmlFriendlyNameCoder("_-", "_")));
        x.autodetectAnnotations(true);
        x.processAnnotations(HpsmMessage.class);
        return x;
    }

    @Bean
    public HpsmEnvConfig getHpsmSetup () {

        try {
            return HpsmEnvConfig.load(HPSM_CONFIG_XML_FILE);
        }
        catch (IOException ex) {
            throw new RuntimeException("Unable to load HPSM-configuration, check file " + HPSM_CONFIG_XML_FILE, ex);
        }
    }

    @Bean
    public CompanyBranchMap companyBranchMap () {
        return new CompanyBranchMap();
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

    @Bean(name = "hpsmMailFactory")
    public MailMessageFactory createHpsmMailFactory() {
        return new JavaMailMessageFactory (mailSender());
    }

    @Bean
    public HpsmMessageFactory createHpsmMessageFactory() {
        return new HpsmMessageFactoryImpl();
    }


    @Bean(name = "hpsmSendChannel")
    public MailSendChannel getRealMailSendChannel () {
        return new JavaMailSendChannel(mailSender());
    }


    @Bean
    public InboundPingMessageHandler getPingCommandHandler () {
        return new InboundPingMessageHandler();
    }

    @Bean
    public InboundMainMessageHandler getMainEventHandler () {
        return new InboundMainMessageHandler();
    }

    @Bean
    public BackChannelHandlerFactory backChannelHandlerFactory () {
        return new BackChannelHandlerFactoryImpl();
    }

    @Bean
    public HpsmService getHpsmService () {
        return new HpsmServiceImpl(getPingCommandHandler(), getMainEventHandler());
    }




    @Bean
    @Scope("prototype")
    public EventMsgInputStreamSource eventMsgInputStreamSource () {
        return new EventMsgInputStreamSource();
    }
}
