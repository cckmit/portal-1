package ru.protei.portal.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.MockTestConfiguration;
import ru.protei.portal.config.TestNotificationConfiguration;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.mock.AuthServiceMock.TEST_AUTH_TOKEN;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestNotificationConfiguration.class, MockTestConfiguration.class})
public class CaseLinkServiceImplTest {


    @InjectMocks
    CaseLinkService caseLinkService = new CaseLinkServiceImpl();

    @Autowired
    CaseService caseService;
    @Autowired
    CaseLinkDAO caseLinkDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    EventPublisherService publisherService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks( this );
    }

    @Test
    public void sendMailNotificationOnAddLinks() {

        CaseObject caseObject = new CaseObject();
        caseObject.setLinks( CollectionUtils.listOf( new CaseLink() ) );

        when( caseObjectDAO.getCaseIdByNumber( eq( CASE_NUMBER ) ) ).thenReturn( CASE_ID );
        when( caseObjectDAO.getCaseByCaseno( eq( CASE_NUMBER ) ) ).thenReturn( caseObject );
        when( caseLinkDAO.getListByQuery( any( CaseLinkQuery.class ) ) ).thenReturn( Collections.EMPTY_LIST );
        when( caseLinkDAO.persist( any( CaseLink.class ) ) ).thenReturn( CASELINK_ID );
        when( caseService.sendMailNotificationLinkChanged( eq( CASE_NUMBER ), any() ) ).thenReturn( ok( CASELINK_ID ) );

        Long link_id = caseLinkService.addYoutrackLink( TEST_AUTH_TOKEN, CASE_NUMBER, "YouTrack_ID" ).getData();
        assertEquals("Expected id of added lik",  CASELINK_ID, link_id );

        verify( caseService, atLeastOnce() ).sendMailNotificationLinkChanged( anyLong(), any() );
    }

    public static final Long CASE_NUMBER = 111L;
    public static final Long CASE_ID = 222L;
    public static final Long CASELINK_ID = 333L;


}