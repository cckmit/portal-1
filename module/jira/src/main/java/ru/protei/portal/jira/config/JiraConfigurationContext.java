package ru.protei.portal.jira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.factory.JiraClientFactoryImpl;
import ru.protei.portal.jira.service.JiraBackchannelHandler;
import ru.protei.portal.jira.service.JiraBackchannelHandlerImpl;
import ru.protei.portal.jira.service.JiraEventHandlerImpl;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.service.JiraIntegrationServiceImpl;

@Configuration
@EnableWebMvc
public class JiraConfigurationContext {

    @Bean
    public JiraClientFactory getJiraClientFactory () {
        return new JiraClientFactoryImpl();
    }

    @Bean
    public JiraEventHandlerImpl getJiraEventHandler() {
        return new JiraEventHandlerImpl();
    }

    @Bean
    public JiraIntegrationService getJiraService () {
        return new JiraIntegrationServiceImpl();
    }

    @Bean
    public JiraBackchannelHandler getJiraBackchannelHandler() {
        return new JiraBackchannelHandlerImpl();
    }
}
