package ru.protei.portal.hpsm;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Created by michael on 19.04.17.
 */
@Configuration
public class HpsmConfiguration {

    @Bean(name = "sender")
    public JavaMailSender mailSender () {
        JavaMailSenderImpl impl = new org.springframework.mail.javamail.JavaMailSenderImpl ();

        impl.setDefaultEncoding("utf-8");
        impl.setHost("smtp.protei.ru");
        impl.setPort(2525);


        return impl;
    }


    @Bean
    public XStream xstreamSerializer () {
        XStream x = new XStream(new Xpp3Driver(new XmlFriendlyNameCoder("_-", "_")));

        x.autodetectAnnotations(true);

        return x;
    }


    @Bean
    @Scope("prototype")
    public EventMsgInputStreamSource eventMsgInputStreamSource () {
        return new EventMsgInputStreamSource ();
    }
}
