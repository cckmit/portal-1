package ru.protei.portal.redmine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.protei.portal.redmine.handlers.BackchannelEventHandler;
import ru.protei.portal.redmine.handlers.ForwardChannelEventHandler;
import ru.protei.portal.redmine.handlers.RedmineBackChannelHandler;
import ru.protei.portal.redmine.handlers.RedmineForwardChannel;
import ru.protei.portal.redmine.service.*;

@Configuration
@EnableScheduling
public class RedmineConfigurationContext {

    @Bean
    public RedmineBootstrapService getRedmineBootstrapService() {
        return new RedmineBootstrapService();
    }

    @Bean
    public BackchannelEventHandler getBackChannelUpdateIssueHandler() {
        return new RedmineBackChannelHandler();
    }

    @Bean
    public ForwardChannelEventHandler getForwardChannelEventHandler() {
        return new RedmineForwardChannel();
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
