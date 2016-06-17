package ru.protei.portal.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

@SpringBootApplication
@EnableAutoConfiguration(exclude =
        LiquibaseAutoConfiguration.class
)
@Import({CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class})
public class Main {

    public static void main(String[] args) {
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
//                CoreConfigurationContext.class,
//                JdbcConfigurationContext.class,
//                MainConfiguration.class
//                );


        SpringApplication.run(Main.class, args);
        //ctx.close();
        //это если дальше спринг использовать не хочешь
      //  MyEntityDAO myEntityDAO = ctx.getBean(MyEntityDAO.class);
    }
}
