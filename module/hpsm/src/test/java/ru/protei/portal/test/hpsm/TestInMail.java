package ru.protei.portal.test.hpsm;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.integration.core.MessageSource;
import ru.protei.portal.hpsm.service.HpsmService;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;
import ru.protei.portal.test.hpsm.config.RawMailTestConfig;


/**
 * Created by michael on 16.05.17.
 */
public class TestInMail {

    @Test
    public void test001 () {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext (RawMailTestConfig.class);

        MessageSource<Object> source = ctx.getBean("rawTestSource", MessageSource.class);

        Object x = source.receive();

        if (x != null)
            System.out.println(x);
        else
            System.out.println("no data");
    }


    @Test
    public void test002 () {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext (HpsmTestConfiguration.class);

        ctx.getBean(HpsmService.class).handleInboundRequest();
    }
}
