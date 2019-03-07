package ru.protei.portal.jira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.factory.JiraClientFactoryImpl;
import ru.protei.portal.jira.handlers.JiraBackchannelHandler;
import ru.protei.portal.jira.handlers.JiraBackchannelHandlerImpl;
import ru.protei.portal.jira.handlers.JiraEventHandler;
import ru.protei.portal.jira.handlers.JiraEventHandlerImpl;
import ru.protei.portal.jira.service.JiraIntegrationService;

@Configuration
@EnableWebMvc
public class JiraConfigurationContext {

    @Bean
    public JiraClientFactory getJiraClientFactory () {
        return new JiraClientFactoryImpl();
    }

    @Bean
    public JiraEventHandler getJiraEventHandler() {
        return new JiraEventHandlerImpl();
    }

    @Bean
    public JiraIntegrationService getJiraService () {
        return new JiraIntegrationService();
    }

    @Bean
    public JiraBackchannelHandler getJiraBackchannelHandler() {
        return new JiraBackchannelHandlerImpl();
    }
}
