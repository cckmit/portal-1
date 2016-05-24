package ru.protei.portal.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class,
                HttpConfigurationContext.class);

        //это если дальше спринг использовать не хочешь
      //  MyEntityDAO myEntityDAO = ctx.getBean(MyEntityDAO.class);
    }
}
