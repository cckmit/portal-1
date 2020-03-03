package ru.protei.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiImpl;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClientImpl;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapperImpl;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.AsyncEventPublisherService;
import ru.protei.portal.core.service.events.EventAssemblerService;
import ru.protei.portal.core.service.events.EventAssemblerServiceImpl;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.policy.PolicyServiceImpl;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.redmine.aspect.RedmineServiceLayerInterceptor;
import ru.protei.portal.redmine.handlers.BackchannelEventHandler;
import ru.protei.portal.redmine.handlers.ForwardChannelEventHandler;
import ru.protei.portal.redmine.handlers.RedmineBackChannelHandler;
import ru.protei.portal.redmine.handlers.RedmineForwardChannel;
import ru.protei.portal.redmine.service.*;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.impl.LockServiceImpl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@EnableAspectJAutoProxy
@EnableAsync
@Configuration
public class RedmineTestConfiguration {

    @Bean
    public RedmineServiceLayerInterceptor getRedmineServiceLayerInterceptor() {
        return new RedmineServiceLayerInterceptor();
    }

    @Bean
    public SessionService getSessionService  (  ) {
        return mock( SessionService.class);
    }

    @Bean
    public FileController getFileController() {
        return new FileController();
    }

    @Bean
    public FileStorage getFileStorage( @Autowired PortalConfig config ) {
        PortalConfigData.CloudConfig cloud = config.data().cloud();
        return new FileStorage( cloud.getStoragePath(), cloud.getUser(), cloud.getPassword() );
    }

    @Bean
    public AuthService getAuthService() {
        return new AuthServiceMock();
    }

    @Bean
    public PolicyService getPolicyService() {
        return new PolicyServiceImpl();
    }

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

    @Bean
    public EventPublisherService getEventPublisherService() {
        return new AsyncEventPublisherService();
    }


    @Bean
    public CaseService getCaseService() {
        return new CaseServiceImpl();
    }

    @Bean
    public CompanyService getCompanyService() {
        return new CompanyServiceImpl();
    }

    @Bean
    public AttachmentService getAttachmentService() {
        return new AttachmentServiceImpl();
    }

    @Bean
    public CaseLinkService getCaseLinkService() {
        return new CaseLinkServiceImpl();
    }

    @Bean
    public CaseCommentService getCaseCommentService() {
        return new CaseCommentServiceImpl();
    }

    @Bean
    public CaseStateWorkflowService getCaseStateWorkflowService() {
        return new CaseStateWorkflowServiceImpl();
    }

    @Bean
    public CaseTagService getCaseTagService() {
        return new CaseTagServiceImpl();
    }

    @Bean
    public EventAssemblerService getEventAssemblerService() {
        return new EventAssemblerServiceImpl();
    }

    @Bean
    public AssemblerService getAssemblerService() {
        return new AssemblerServiceImpl();
    }

    @Bean
    public YoutrackService getYoutrackService() {
        return new YoutrackServiceImpl();
    }

    @Bean
    public YoutrackHttpClient getYoutrackHttpClient() {
        return new YoutrackHttpClientImpl();
    }

    @Bean
    public YoutrackApi getYoutrackApi() {
        return new YoutrackApiImpl();
    }

    @Bean
    public YtDtoFieldsMapper getYtDtoFieldsMapper() {
        return new YtDtoFieldsMapperImpl();
    }

    @Bean
    public LockService getLockService() {
        return new LockServiceImpl();
    }


}
