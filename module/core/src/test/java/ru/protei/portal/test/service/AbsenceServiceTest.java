package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.service.AbsenceService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, IntegrationTestsConfiguration.class})
public class AbsenceServiceTest extends BaseServiceTest {

    @Test
    public void testAbsence() {
        /* create */
        PersonAbsence absence = createAbsence(1L, En_AbsenceReason.PERSONAL_AFFAIR);
        Assert.assertNotNull("Absence not created", absenceService.createAbsence(getAuthToken(), absence));

        /* update */
        absence.setTillTime(new Date(absence.getTillTime().getTime() + 600000L));
        absence.setUserComment("Test comment");
        Assert.assertNotNull("Absence not updated", absenceService.updateAbsence(getAuthToken(), absence));

        /* complete */
        Assert.assertNotNull("Absence not completed", absenceService.completeAbsence(getAuthToken(), absence.getId()));

        /* remove */
        Assert.assertNotNull("Absence not removed", absenceService.removeAbsence(getAuthToken(), absence.getId()));
    }

    @Autowired
    AbsenceService absenceService;
}
