package ru.protei.portal.redmine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.protei.portal.redmine.factory.CaseUpdaterFactory;
import ru.protei.portal.redmine.handlers.RedmineBackChannelHandler;
import ru.protei.portal.redmine.handlers.RedmineForwardIssueChannel;
import ru.protei.portal.redmine.service.*;

@Configuration
@EnableScheduling
public class RedmineConfigurationContext {

    @Bean
    public RedmineBootstrapService getRedmineBootstrapService() {
        return new RedmineBootstrapService();
    }

    @Bean
    public RedmineBackChannelHandler getBackchannelUpdateIssueHandler() {
        return new RedmineBackChannelHandler();
    }

    @Bean
    public RedmineForwardIssueChannel getRedmineNewIssueHandler() {
        return new RedmineForwardIssueChannel();
    }

//    @Bean
//    public RedmineUpdateIssueHandler getRedmineUpdateIssueHandler() {
//        return new RedmineUpdateIssueHandler();
//    }

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
