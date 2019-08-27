package ru.protei.portal.test.jira;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.test.jira.config.JiraTestConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

@RunWith( SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, /*DatabaseConfiguration.class,*/ JiraTestConfiguration.class})
public class IssueMappingTest {

    @Test
    public void createIssue() {
    }

    @Test
    public void updateIssue() {
    }

    @Test
    public void createIssueComment() {
    }
}
