package ru.protei.portal.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.DaoMockTestConfiguration;
import ru.protei.portal.config.PortalConfigTestConfiguration;
import ru.protei.portal.config.ServiceTestsConfiguration;
import ru.protei.portal.config.TestEventConfiguration;
import ru.protei.portal.core.model.api.ApiAbsence;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.dao.PersonNotifierDAO;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.service.AbsenceService;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        ServiceTestsConfiguration.class,
        DaoMockTestConfiguration.class,
        TestEventConfiguration.class
})
public class AbsenceServiceUnitTest extends BaseServiceTest {

    private static final Long ABSENCE_ID = 1L;
    private static final Long TIME_DIFFERENCE = 1000000L;

    @Autowired
    AbsenceService absenceService;
    @Autowired
    PersonAbsenceDAO personAbsenceDAO;

    @Autowired
    private PersonNotifierDAO personNotifierDAO;

    @Test
    public void checkIsAbsenceCreatedFromPortal() {
        PersonAbsence absence = createAbsence(ABSENCE_ID);
        absence.setTillTime(new Date(absence.getFromTime().getTime() + TIME_DIFFERENCE));

        when(personAbsenceDAO.listByEmployeeAndDateBounds(anyLong(), any(Date.class), any(Date.class)))
                             .thenReturn(new ArrayList<>());
        when(personAbsenceDAO.get(anyLong())).thenReturn(null);
        when(personNotifierDAO.getByPersonId(anyLong())).thenReturn(new ArrayList<>());

        ArgumentCaptor<PersonAbsence> requestCaptor = forClass(PersonAbsence.class);
        absenceService.createAbsenceFromPortal(getAuthToken(), absence);

        verify(personAbsenceDAO, atLeastOnce()).persist(requestCaptor.capture());
        PersonAbsence savedAbsence = requestCaptor.getValue();
        assertFalse("Expected isCreatedFrom1C flag is false", savedAbsence.isCreatedFrom1C());
    }

    @Test
    public void checkIsAbsenceCreatedFromPortalNegative() {
        PersonAbsence absence = createAbsence(ABSENCE_ID);
        absence.setTillTime(new Date(absence.getFromTime().getTime() + TIME_DIFFERENCE));

        absence.setCreatedFrom1C(true);

        when(personAbsenceDAO.listByEmployeeAndDateBounds(anyLong(), any(Date.class), any(Date.class))).thenReturn(new ArrayList<>());
        when(personAbsenceDAO.get(anyLong())).thenReturn(null);
        when(personNotifierDAO.getByPersonId(anyLong())).thenReturn(new ArrayList<>());

        ArgumentCaptor<PersonAbsence> requestCaptor = forClass(PersonAbsence.class);
        absenceService.createAbsenceFromPortal(getAuthToken(), absence);

        verify(personAbsenceDAO, atLeastOnce()).persist(requestCaptor.capture());
        PersonAbsence savedAbsence = requestCaptor.getValue();
        assertFalse("Expected isCreatedFrom1C flag is false", savedAbsence.isCreatedFrom1C());
    }

    @Test
    public void checkIsAbsenceCreatedFrom1CByApi() {
        ApiAbsence apiAbsence = new ApiAbsence();
        apiAbsence.setWorkerExtId("0000000001");
        apiAbsence.setCompanyCode("companyCode");
        apiAbsence.setReason(En_AbsenceReason.REMOTE_WORK);
        apiAbsence.setFromTime(new Date());
        apiAbsence.setTillTime((new Date(apiAbsence.getFromTime().getTime() + TIME_DIFFERENCE)));
        apiAbsence.setPersonId(ABSENCE_ID);

        ArgumentCaptor<PersonAbsence> requestCaptor = forClass(PersonAbsence.class);
        absenceService.createAbsenceByApi(getAuthToken(), apiAbsence);

        verify(personAbsenceDAO, atLeastOnce()).persist(requestCaptor.capture());

        PersonAbsence savedAbsence = requestCaptor.getValue();
        assertTrue("Expected isCreatedFrom1C flag is true", savedAbsence.isCreatedFrom1C());
    }
}