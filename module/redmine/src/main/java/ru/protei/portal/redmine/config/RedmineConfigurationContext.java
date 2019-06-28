package ru.protei.portal.redmine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.protei.portal.redmine.factory.CaseUpdaterFactory;
import ru.protei.portal.redmine.handlers.RedmineBackChannelHandler;
import ru.protei.portal.redmine.handlers.RedmineNewIssueHandler;
import ru.protei.portal.redmine.handlers.RedmineUpdateIssueHandler;
import ru.protei.portal.redmine.service.*;

@Configuration
@EnableScheduling
public class RedmineConfigurationContext {

    @Bean
    public BootstrapService getBootstrapService() {
        return new BootstrapService();
    }

    @Bean
    public RedmineBackChannelHandler getBackchannelUpdateIssueHandler() {
        return new RedmineBackChannelHandler();
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

    @Bean
    public CaseUpdaterFactory getCaseUpdaterFactory() {
        return new CaseUpdaterFactory();
    }
}
