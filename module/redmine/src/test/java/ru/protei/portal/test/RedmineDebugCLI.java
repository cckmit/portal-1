package ru.protei.portal.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.redmine.config.RedmineConfigurationContext;
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

        RedmineEndpointDAO redmineEndpointDAO = ctx.getBean(RedmineEndpointDAO.class);

        RedmineService service = ctx.getBean(RedmineService.class);

        //redmineEndpointDAO.getAll().forEach(service::checkForNewIssues);
        redmineEndpointDAO.getAll().forEach(service::checkForUpdatedIssues);
    }
}
