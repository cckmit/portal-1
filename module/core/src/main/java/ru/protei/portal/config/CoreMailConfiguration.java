package ru.protei.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import ru.protei.portal.core.mail.*;

@Configuration
public class CoreMailConfiguration {
    /**
     * Mail
     */
    @Bean(name = "coreMailSender")
    public JavaMailSender mailSender ( @Autowired PortalConfig config ) throws Exception {

        PortalConfigData.SmtpConfig smtp = config.data().smtp();

        JavaMailSenderImpl impl = new JavaMailSenderImpl ();

        impl.setDefaultEncoding(smtp.getDefaultCharset());
        impl.setHost(smtp.getHost());
        impl.setPort(smtp.getPort());

        return impl;
    }

    @Bean(name = "coreMimeMessageProvider")
    public MimeMessageProvider getMimeMessageProvider( @Autowired PortalConfig config ) {

        PortalConfigData.SmtpConfig smtp = config.data().smtp();

        MimeMessageProvider provider = new JavaMimeMessageProvider();
        provider.setMessageIdPattern(smtp.getMessageIdPattern());
        return provider;
    }

    @Bean(name = "coreMailMessageFactory")
    public MailMessageFactory createMailMessageFactory( @Autowired PortalConfig config ) throws Exception {
        return new JavaMailMessageFactory(mailSender( config ), getMimeMessageProvider( config ) );
    }

    @Bean(name = "coreMailSendChannel")
    public MailSendChannel getMailSendChannel ( @Autowired PortalConfig config ) throws Exception {
        return new JavaMailSendChannel(mailSender( config ));
    }
}
