package ru.protei.portal.redmine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.config.PortalConfigData;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiImpl;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClientImpl;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapperImpl;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.model.dao.DeliveryDAO;
import ru.protei.portal.core.model.dao.impl.DeliveryDAO_Impl;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseService;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseTaskHandler;
import ru.protei.portal.core.service.events.*;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.policy.PolicyServiceImpl;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.redmine.aspect.RedmineServiceLayerInterceptor;
import ru.protei.portal.redmine.handlers.BackchannelEventHandler;
import ru.protei.portal.redmine.handlers.ForwardChannelEventHandler;
import ru.protei.portal.redmine.handlers.RedmineBackChannelHandler;
import ru.protei.portal.redmine.handlers.RedmineForwardChannel;
import ru.protei.portal.redmine.service.*;
import ru.protei.portal.schedule.PortalScheduleTasks;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.impl.LockServiceImpl;
import ru.protei.portal.core.service.UitsService;
import ru.protei.portal.core.service.UitsServiceImpl;

import static org.mockito.Mockito.mock;

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
    @Lazy
    public ProjectService getProjectService() {
        return new ProjectServiceImpl();
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

/*
    @Bean
    public ClientEventService getClientEventService() {
        return new ClientEventServiceImpl();
    }
*/

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
    public EventProjectAssemblerService getProjectPublisherService() {
        return new EventProjectAssemblerServiceImpl();
    }

    @Bean
    public EventDeliveryAssemblerService getDeliveryPublisherService() {
        return new EventDeliveryAssemblerServiceImpl();
    }

    @Bean
    public AssemblerDeliveryService getAssemblerDeliveryService() {
        return new AssemblerDeliveryServiceImpl();
    }

    @Bean
    public DeliveryDAO getDeliveryDAO() {
        return new DeliveryDAO_Impl();
    }

    @Bean
    @Lazy
    public DeliveryService getDeliveryService() {
        return new DeliveryServiceImpl();
    }

    @Bean
    public CardService getCardService() {
        return new CardServiceImpl();
    }

    @Bean
    public CardBatchService getCardBatchService() {
        return new CardBatchServiceImpl();
    }

    @Bean
    public AssemblerService getAssemblerService() {
        return new AssemblerServiceImpl();
    }

    @Bean
    public AssemblerProjectService getAssemblerProjectService() {
        return new AssemblerProjectServiceImpl();
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
    public UitsService getUitsService() {
        return new UitsServiceImpl();
    }

    @Bean
    public LockService getLockService() {
        return new LockServiceImpl();
    }

    @Bean
    public AutoOpenCaseService getAutoOpenCaseService() {
        return mock(AutoOpenCaseService.class);
    }

    @Bean
    public AutoOpenCaseTaskHandler getAutoOpenCaseTaskHandler() {
        return mock(AutoOpenCaseTaskHandler.class);
    }

    @Bean
    public SiteFolderService getSiteFolderService() {
        return mock(SiteFolderService.class);
    }

    @Bean
    public ProductService getProductService() {
        return mock(ProductService.class);
    }

    @Bean
    public PlanService getPlanService() {
        return mock(PlanService.class);
    }

    @Bean
    public PortalScheduleTasks getPortalScheduleTasks() {
        return mock(PortalScheduleTasks.class);
    }

    @Bean
    public HistoryService getHistoryService() {
        return new HistoryServiceImpl();
    }
}
