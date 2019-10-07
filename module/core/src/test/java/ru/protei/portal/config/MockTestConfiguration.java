package ru.protei.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.EventPublisherService;
import ru.protei.portal.core.service.events.AsyncEventPublisherService;

import static org.mockito.Mockito.mock;

@Configuration
public class MockTestConfiguration {

    @Bean
    public CaseService getCaseService() {
        return mock( CaseService.class );
    }

    @Bean
    public CaseLinkDAO getCaseLinkDAO() {
        return mock( CaseLinkDAO.class );
    }

    @Bean
    public CaseObjectDAO getCaseDAO() {
        return mock( CaseObjectDAO.class );
    }

    @Bean
    public EventPublisherService getEventPublisherService() {
        return mock( AsyncEventPublisherService.class );
    }

}