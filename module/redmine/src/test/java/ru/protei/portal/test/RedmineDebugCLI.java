package ru.protei.portal.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.redmine.config.RedmineConfigurationContext;
import ru.protei.portal.redmine.handlers.RedmineForwardChannel;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.service.CommonServiceImpl;
import ru.protei.portal.redmine.service.RedmineService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

public class RedmineDebugCLI {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class,
                RedmineConfigurationContext.class,
                DebugConfContext.class
        );


        RedmineForwardChannel service = ctx.getBean(RedmineForwardChannel.class);

        service.checkIssues();
    }
}
