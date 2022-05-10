package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.report.absence.ReportAbsence;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class,
        RemoteServiceFactory.class, HttpClientFactory.class, HttpConfigurationContext.class})
@Transactional
public class ReportAbsenceTest extends BaseServiceTest {

    private void initData() {
        Company company = makeCompany(En_CompanyCategory.HOME);
        Person person1 = makePerson(company);
        Person person2 = makePerson(company);
        Person person3 = makePerson(company);
        makeAbsence(person1.getId());
        makeAbsence(person2.getId());
        makeAbsence(person3.getId());
    }

    @Test
    public void testReport() throws IOException {
        initData();

        AbsenceQuery query = makeAbsenceQuery();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        boolean result = reportAbsence.writeReport(buffer, query);

        Assert.assertTrue("Report failed", result);
        Assert.assertTrue("Expected not empty report data", buffer.size() > 0);
    }

    private AbsenceQuery makeAbsenceQuery() {

        Date till = new Date();
        Date from = new Date(till.getTime() - 600000L);

        return new AbsenceQuery(new DateRange(En_DateIntervalType.FIXED, from, till));
    }

    @Autowired
    ReportAbsence reportAbsence;
}
