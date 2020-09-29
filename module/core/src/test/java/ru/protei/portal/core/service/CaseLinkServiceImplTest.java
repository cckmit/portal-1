package ru.protei.portal.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.DaoMockTestConfiguration;
import ru.protei.portal.config.PortalConfigTestConfiguration;
import ru.protei.portal.config.ServiceTestsConfiguration;
import ru.protei.portal.config.TestEventConfiguration;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventAssemblerService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.mock.AuthServiceMock;

import java.util.Collections;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        DaoMockTestConfiguration.class,
        ServiceTestsConfiguration.class,
        TestEventConfiguration.class
})
public class CaseLinkServiceImplTest {

    @Autowired
    CaseLinkService caseLinkService;
    @Autowired
    CaseLinkDAO caseLinkDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseService caseService;
    @Autowired
    AuthService authService;

    @Autowired
    EventPublisherService publisherService;
    @Autowired
    EventAssemblerService assemblerService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks( this );
    }

    @Test
    public void sendMailNotificationOnSetLinks() {

        when( caseObjectDAO.getCaseIdByNumber( eq(En_CaseType.CRM_SUPPORT), eq(CASE_NUMBER ) ) ).thenReturn( CASE_ID );
        when( caseObjectDAO.getCaseByNumber( eq(En_CaseType.CRM_SUPPORT), eq( CASE_NUMBER ) ) ).thenReturn( new CaseObject() );
        when( caseLinkDAO.getListByQuery( any( CaseLinkQuery.class ) ) ).thenReturn( Collections.EMPTY_LIST );
        when( caseLinkDAO.persist( any( CaseLink.class ) ) ).thenReturn( CASELINK_ID );

        caseLinkService.setYoutrackIdToCaseNumbers( ((AuthServiceMock) authService).getAuthToken(), "YouTrack_ID", Collections.singletonList(CASE_NUMBER) );

        verify( publisherService, atLeastOnce() ).publishEvent( any() );
    }

    public static final Long CASE_NUMBER = 111L;
    public static final Long CASE_ID = 222L;
    public static final Long CASELINK_ID = 333L;


}