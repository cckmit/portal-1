package ru.protei.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import ru.protei.portal.core.aspect.ServiceLayerInterceptor;
import ru.protei.portal.core.controller.auth.AuthInterceptor;
import ru.protei.portal.core.mail.JavaMailMessageFactory;
import ru.protei.portal.core.mail.JavaMailSendChannel;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.core.service.user.AuthServiceImpl;
import ru.protei.portal.core.service.user.LDAPAuthProvider;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.core.utils.SimpleSidGenerator;
import ru.protei.winter.core.utils.config.exception.ConfigException;

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

    @Bean(name = "coreMailMessageFactory")
    public MailMessageFactory createMailMessageFactory( @Autowired PortalConfig config ) throws Exception {
        return new JavaMailMessageFactory(mailSender( config ));
    }

    @Bean(name = "coreMailSendChannel")
    public MailSendChannel getMailSendChannel ( @Autowired PortalConfig config ) throws Exception {
        return new JavaMailSendChannel(mailSender( config ));
    }
}
