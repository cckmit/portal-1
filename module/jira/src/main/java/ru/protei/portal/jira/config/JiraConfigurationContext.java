package ru.protei.portal.jira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.jira.factory.JiraEventTypeHandlersFactory;
import ru.protei.portal.jira.factory.JiraIssueCreatedEventHandler;
import ru.protei.portal.jira.factory.JiraIssueUpdatedEventHandler;
import ru.protei.portal.jira.handlers.JiraEventHandler;
import ru.protei.portal.jira.handlers.JiraEventHandlerImpl;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.protei.portal.jira.handlers")
public class JiraConfigurationContext {
    @Bean
    public JiraEventHandler getJiraEventHandler() {
        return new JiraEventHandlerImpl();
    }

    @Bean
    public JiraIssueCreatedEventHandler getJiraIssueCreatedEventHandler() { return new JiraIssueCreatedEventHandler(); }

    @Bean
    public JiraIssueUpdatedEventHandler getJiraIssueUpdatedEventHandler() { return new JiraIssueUpdatedEventHandler(); }
}
