package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.service.AbsenceService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JdbcConfigurationContext.class, DatabaseConfiguration.class,
        IntegrationTestsConfiguration.class, RemoteServiceFactory.class, HttpClientFactory.class, HttpConfigurationContext.class})
public class AbsenceServiceIntegrationTest extends BaseServiceTest {

    @Test
    public void testAbsence() {
        /* create */
        PersonAbsence absence = createAbsence(1L);
        Assert.assertNotNull("Absence not created", absenceService.createAbsenceFromPortal(getAuthToken(), absence));

        /* update */
        absence.setTillTime(new Date(absence.getTillTime().getTime() + 600000L));
        absence.setUserComment("Test comment");
        Assert.assertNotNull("Absence not updated", absenceService.updateAbsence(getAuthToken(), absence));

        /* complete */
        Assert.assertNotNull("Absence not completed", absenceService.completeAbsence(getAuthToken(), absence));

        /* remove */
        Assert.assertNotNull("Absence not removed", absenceService.removeAbsence(getAuthToken(), absence));
    }

    @Autowired
    AbsenceService absenceService;
}
