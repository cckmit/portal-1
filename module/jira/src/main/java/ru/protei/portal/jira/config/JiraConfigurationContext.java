package ru.protei.portal.jira.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.mail.VirtualMailSendChannel;
import ru.protei.portal.jira.aspect.JiraServiceLayerInterceptorLogging;
import ru.protei.portal.jira.controller.JiraEventController;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.factory.JiraClientFactoryImpl;
import ru.protei.portal.jira.service.*;
import ru.protei.portal.jira.utils.JiraQueueSingleThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableWebMvc
@EnableAsync
public class JiraConfigurationContext {

    @Autowired
    PortalConfig config;

    /**
     * Запуск фоновых задач
     */
    @Bean(name = JIRA_INTEGRATION_SINGLE_TASK_QUEUE)
    public Executor threadPoolTaskExecutor() {
        int queueLimit = config.data().jiraConfig().getQueueLimit();
        return new JiraQueueSingleThreadPoolTaskExecutor( queueLimit );
    }

    @Bean
    public JiraClientFactory getJiraClientFactory () {
        return new JiraClientFactoryImpl();
    }

    @Bean
    public JiraEventController getJiraEventHandler() {
        return new JiraEventController();
    }


    @Bean
    public JiraIntegrationService getJiraService () {
        return new JiraIntegrationServiceImpl();
    }

    @Bean
    public JiraBackchannelHandler getJiraBackchannelHandler() {
        return new JiraBackchannelHandlerImpl();
    }


    @Bean
    public JiraServiceLayerInterceptorLogging getJiraServiceLayerInterceptorLogging() {
        return new JiraServiceLayerInterceptorLogging();
    }

    public static final String JIRA_INTEGRATION_SINGLE_TASK_QUEUE = "jira_integration_single_task_queue";
}
