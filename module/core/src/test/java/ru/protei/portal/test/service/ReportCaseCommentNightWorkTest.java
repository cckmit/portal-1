package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dao.CaseCommentNightWorkDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.report.nightwork.ReportNightWork;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static ru.protei.portal.core.model.util.CrmConstants.NightWork.HOUR_OFFSET;
import static ru.protei.portal.core.model.util.CrmConstants.Time.MINUTE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class})
@Transactional
public class ReportCaseCommentNightWorkTest extends BaseServiceTest {
    @Test
    public void testReport() throws IOException {
        Company home = makeCompany(En_CompanyCategory.HOME);
        Person person1 = makePerson(home);
        CaseObject caseObject1 = makeCaseObject(person1);

        /**
         Днем считается с 21:00 - 21:00, что прописано в
         @JdbcEntity(selectSql
         ru.protei.portal.core.model.ent.CaseCommentNightWork
         */
        CaseComment comment11 = createNewComment(new GregorianCalendar(2021, Calendar.MARCH, 23, 20, 59, 0).getTime(),
                person1, caseObject1.getId(), "before period");
        comment11.setTimeElapsed(1000 * MINUTE);
        comment11.setTimeElapsedType(En_TimeElapsedType.NIGHT_WORK);
        caseCommentDAO.persist(comment11);

        CaseComment comment12 = createNewComment(new GregorianCalendar(2021, Calendar.MARCH, 23, 21, 1, 0).getTime(),
                person1, caseObject1.getId(), "after begin period");
        comment12.setTimeElapsed(1 * MINUTE);
        comment12.setTimeElapsedType(En_TimeElapsedType.NIGHT_WORK);
        caseCommentDAO.persist(comment12);

        CaseComment comment13 = createNewComment(new GregorianCalendar(2021, Calendar.MARCH, 24, 6, 59, 0).getTime(),
                person1, caseObject1.getId(), "middle period");
        comment13.setTimeElapsed(11 * MINUTE);
        comment13.setTimeElapsedType(En_TimeElapsedType.NIGHT_WORK);
        caseCommentDAO.persist(comment13);

        Person person2 = makePerson(home);
        CaseObject caseObject2 = makeCaseObject(person1);

        CaseComment comment21 = createNewComment(new GregorianCalendar(2021, Calendar.MARCH, 24, 20, 59, 0).getTime(),
                person2, caseObject2.getId(), "before end period");
        comment21.setTimeElapsed(21 * MINUTE);
        comment21.setTimeElapsedType(En_TimeElapsedType.NIGHT_WORK);
        caseCommentDAO.persist(comment21);

        CaseComment comment22 = createNewComment(new GregorianCalendar(2021, Calendar.MARCH, 24, 21, 1, 0).getTime(),
                person2, caseObject2.getId(), "after end period");
        comment22.setTimeElapsed(1000 * MINUTE);
        comment22.setTimeElapsedType(En_TimeElapsedType.NIGHT_WORK);
        caseCommentDAO.persist(comment22);

        CaseQuery query = new CaseQuery();
        query.setCreatedRange(new DateRange(En_DateIntervalType.FIXED,
                new GregorianCalendar(2021, Calendar.MARCH, 23, 24+HOUR_OFFSET, 0, 0).getTime(),
                new GregorianCalendar(2021, Calendar.MARCH, 24, 24+HOUR_OFFSET, 0, 0).getTime())
        );
        query.setSortDir(En_SortDir.ASC);
        query.setSortField(En_SortField.day);
        List<CaseCommentNightWork> list = caseCommentNightWorkDAO.getListByQuery(query);

        Date day = new GregorianCalendar(2021, Calendar.MARCH, 24).getTime();

        Assert.assertEquals("Expect 2 report item", 2, list.size());

        Assert.assertEquals("Expect first item day", day, list.get(0).getDay());
        Assert.assertEquals("Expect first item issue number", caseObject1.getCaseNumber(), list.get(0).getCaseNumber());
        Assert.assertEquals("Expect first item issue nightWork TimeElapsed Count", 2, list.get(0).getNightWorkTimeElapsedCount().intValue());
        Assert.assertEquals("Expect first item issue nightWork TimeElapsed Sum", (1+11)*MINUTE, list.get(0).getNightWorkTimeElapsedSum().intValue());
        Assert.assertEquals("Expect first item issue last comment id", comment13.getId(), list.get(0).getLastCommentId());

        Assert.assertEquals("Expect second item day", day, list.get(1).getDay());
        Assert.assertEquals("Expect second item issue number", caseObject2.getCaseNumber(), list.get(1).getCaseNumber());
        Assert.assertEquals("Expect second item issue nightWork TimeElapsed Count", 1, list.get(1).getNightWorkTimeElapsedCount().intValue());
        Assert.assertEquals("Expect second item issue nightWork TimeElapsed Sum", 21*MINUTE, list.get(1).getNightWorkTimeElapsedSum().intValue());
        Assert.assertEquals("Expect second item issue last comment id", comment21.getId(), list.get(1).getLastCommentId());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Report report = new Report();
        query.setSortDir(null);
        query.setSortField(null);
        report.setQuery(serializeAsJson(query));
        report.setLocale("ru");
        boolean result = reportService.writeReport(buffer, report, query, id -> false);

        Assert.assertTrue("Report failed", result);
        Assert.assertTrue("Expected not empty report data", buffer.size() > 0);
    }

    @Autowired
    CaseCommentNightWorkDAO caseCommentNightWorkDAO;

    @Autowired
    ReportNightWork reportService;
}
