package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.YtWorkQuery;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtEnumBundleElement;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtSingleEnumIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.issue.DurationValue;
import ru.protei.portal.core.model.youtrack.dto.issue.IssueWorkItem;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;
import ru.protei.portal.core.report.ytwork.ReportYtWork;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.mock;
import static ru.protei.portal.core.model.util.CrmConstants.State.PRESALE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class,
        ReportYtWorkTest.ReportYtWorkTestConfiguration.class})
@Transactional
public class ReportYtWorkTest extends BaseServiceTest {

    @Configuration
    public static class ReportYtWorkTestConfiguration {
        @Bean
        public YoutrackApi getYoutrackApi() {
            return mock(YoutrackApi.class);
        }
    }

    @Test
    public void testReport() throws IOException {
        String email = "user1@protei.ru";

        Company home = makeCompany(En_CompanyCategory.HOME);
        Person person1 = createNewPerson(home);
        person1.setLogins(Arrays.asList(email));
        makePerson(person1);

        PersonProjectMemberView personProjectMemberView =
                new PersonProjectMemberView(person1.getDisplayShortName(), person1.getId(), person1.isFired(), En_PersonRoleType.HEAD_MANAGER);

        Company customerCompany = makeCompany(En_CompanyCategory.CUSTOMER);
        Project project1 = new Project();
        project1.setName(TEST_NAME + " : Test_Project 1");
        project1.setDescription(TEST_NAME);
        project1.setStateId(PRESALE);
        project1.setCustomerType(En_CustomerType.STATE_BUDGET);
        project1.setCustomer(customerCompany);
        project1.setTeam(Collections.singletonList(personProjectMemberView));
        project1.setId(projectService.createProject(getAuthToken(), project1).getData().getId());

        Contract guarantee = new Contract();
        guarantee.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 20, 0, 0, 0).getTime());
        guarantee.setNumber(TEST_NAME + " : Guarantee");
        guarantee.setProjectId(project1.getId());

        contractService.createContract(getAuthToken(), guarantee);

        YtWorkQuery query = new YtWorkQuery();
        Date from = new GregorianCalendar(2021, Calendar.JULY, 21, 0, 0, 0).getTime();
        Date to = new GregorianCalendar(2021, Calendar.JULY, 22, 0, 0, 0).getTime();
        query.setDateRange(new DateRange(En_DateIntervalType.FIXED, from, to));

        YtUser ytUser = new YtUser();
        ytUser.email = email;

        YtProject ytProject = new YtProject();
        ytProject.shortName = TEST_NAME + " : Test_Project 1";

        YtEnumBundleElement ytCustomer = new YtEnumBundleElement();
        ytCustomer.localizedName = customerCompany.getCname();
        YtSingleEnumIssueCustomField field = new YtSingleEnumIssueCustomField();
        field.name = YtIssue.CustomFieldNames.cumstomer;
        field.value = ytCustomer;

        YtIssue ytIssue = new YtIssue();
        ytIssue.idReadable = TEST_NAME + " idReadable";
        ytIssue.customFields = Arrays.asList(field);
        ytIssue.project = ytProject;

        DurationValue ytDurationValue = new DurationValue();
        ytDurationValue.minutes = 10;

        IssueWorkItem workItem = new IssueWorkItem();
        workItem.author = ytUser;
        workItem.duration = ytDurationValue;
        workItem.issue = ytIssue;

        List<IssueWorkItem> data = Arrays.asList(workItem);

        Mockito.doReturn(Result.ok(data)).when(api).getWorkItems(from, to, 0, config.data().reportConfig().getChunkSize());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Report report = new Report();
        report.setQuery(serializeAsJson(query));
        report.setLocale("ru");

        boolean result = reportService.writeReport(buffer, report, query, id -> false);

        Assert.assertTrue("Report failed", result);
        Assert.assertTrue("Expected not empty report data", buffer.size() > 0);
    }

    @Autowired
    ReportYtWork reportService;
    @Autowired
    YoutrackApi api;
    @Autowired
    PortalConfig config;

    static private final String TEST_NAME = "ReportYtWorkTest";
}
