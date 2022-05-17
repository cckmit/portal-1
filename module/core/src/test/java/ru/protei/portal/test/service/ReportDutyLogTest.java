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
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.report.dutylog.ReportDutyLog;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class
})
@Transactional
public class ReportDutyLogTest extends BaseServiceTest {

    private void initData() {
        Company company = makeCompany(En_CompanyCategory.HOME);
        Person person1 = makePerson(company);
        Person person2 = makePerson(company);
        Person person3 = makePerson(company);
        makeDutyLog(person1.getId());
        makeDutyLog(person2.getId());
        makeDutyLog(person3.getId());
    }

    @Test
    public void testReport() throws IOException {
        initData();

        DutyLogQuery query = makeQuery();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        boolean result = report.writeReport(buffer, query);

        Assert.assertTrue("Report failed", result);
        Assert.assertTrue("Expected not empty report data", buffer.size() > 0);
    }

    private DutyLogQuery makeQuery() {

        Date till = new Date();
        Date from = new Date(till.getTime() - 600000L);

        return new DutyLogQuery(new DateRange(En_DateIntervalType.FIXED, from, till));
    }

    @Autowired
    ReportDutyLog report;
}
