package ru.protei.portal.redmine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.protei.portal.redmine.handlers.BackchannelUpdateIssueHandler;
import ru.protei.portal.redmine.handlers.RedmineNewIssueHandler;
import ru.protei.portal.redmine.handlers.RedmineUpdateIssueHandler;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.service.CommonServiceImpl;
import ru.protei.portal.redmine.service.RedmineService;
import ru.protei.portal.redmine.service.RedmineServiceImpl;

@Configuration
@EnableScheduling
public class RedmineConfigurationContext {

    @Bean
    public BackchannelUpdateIssueHandler getBackchannelUpdateIssueHandler() {
        return new BackchannelUpdateIssueHandler();
    }

    @Bean
    public RedmineNewIssueHandler getRedmineNewIssueHandler() {
        return new RedmineNewIssueHandler();
    }

    @Bean
    public RedmineUpdateIssueHandler getRedmineUpdateIssueHandler() {
        return new RedmineUpdateIssueHandler();
    }

    @Bean
    public RedmineService getRedmineService() {
        return new RedmineServiceImpl();
    }

    @Bean
    public CommonService getCommonService() {
        return new CommonServiceImpl();
    }
}
