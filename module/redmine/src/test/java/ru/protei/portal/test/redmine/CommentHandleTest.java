package ru.protei.portal.test.redmine;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.redmine.config.RedmineConfigurationContext;
import ru.protei.portal.redmine.service.RedmineService;
import ru.protei.portal.test.DebugConfContext;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

public class CommentHandleTest {

    @Test
    public void testCasualSituation() throws RedmineException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class,
                RedmineConfigurationContext.class,
                DebugConfContext.class
        );

        RedmineEndpointDAO redmineEndpointDAO = ctx.getBean(RedmineEndpointDAO.class);

        RedmineService service = ctx.getBean(RedmineService.class);
        final RedmineEndpoint endpoint = redmineEndpointDAO.getAll().get(0);

        final RedmineManager manager =
                RedmineManagerFactory.createWithApiKey(endpoint.getServerAddress(), endpoint.getApiKey());
        final Issue issue = manager.getIssueManager().getIssueById(65252, Include.journals, Include.attachments, Include.watchers);
    }
}
