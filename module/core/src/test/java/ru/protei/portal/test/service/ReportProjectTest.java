package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.struct.ReportProjectWithComments;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.report.projects.ReportProject;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static ru.protei.portal.core.model.util.CrmConstants.State.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class, DatabaseConfiguration.class,
        IntegrationTestsConfiguration.class})
@Transactional
public class ReportProjectTest extends BaseServiceTest {

    private void init() {
        Company company1 = createNewCompany(REPORT_PROJECT_TEST + " : Test_Company 1", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(company1);
        Company company2 = createNewCompany(REPORT_PROJECT_TEST + " : Test_Company 2", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(company2);

        Person person1 = createNewPerson(company1);
        personDAO.persist(person1);
        Person person2 = createNewPerson(company2);
        personDAO.persist(person2);

        PersonProjectMemberView personProjectMemberView =
                new PersonProjectMemberView(person1.getDisplayShortName(), person1.getId(), person1.isFired(), En_PersonRoleType.HEAD_MANAGER);

        Project project1 = new Project();

        project1.setName(REPORT_PROJECT_TEST + " : Test_Project 1");
        project1.setDescription(REPORT_PROJECT_TEST);
        project1.setStateId(PRESALE);
        project1.setCustomerType(En_CustomerType.STATE_BUDGET);
        project1.setCustomer(company1);
        project1.setTeam(Collections.singletonList(personProjectMemberView));

        project1.setId(projectService.createProject(getAuthToken(), project1).getData().getId());

        caseCommentDAO.persist(createNewComment(person1, project1.getId(), REPORT_PROJECT_TEST + " : Comment 1_1"));
        caseCommentDAO.persist(createNewComment(person1, project1.getId(), REPORT_PROJECT_TEST + " : Comment 1_2"));
        caseCommentDAO.persist(createNewComment(person1, project1.getId(), REPORT_PROJECT_TEST + " : Last Comment 1"));
        caseCommentDAO.persist(createNewComment(person1, project1.getId(), null));

        Project project2 = new Project();

        project2.setName(REPORT_PROJECT_TEST + " : Test_Project 2");
        project2.setDescription(REPORT_PROJECT_TEST);
        project2.setStateId(FINISHED);
        project2.setCustomerType(En_CustomerType.STATE_BUDGET);
        project2.setCustomer(company1);
        project2.setTeam(Collections.singletonList(personProjectMemberView));

        project2.setId(projectService.createProject(getAuthToken(), project2).getData().getId());

        caseCommentDAO.persist(createNewComment(person1, project2.getId(), REPORT_PROJECT_TEST + " : Comment 2"));
        caseCommentDAO.persist(createNewComment(person1, project2.getId(), REPORT_PROJECT_TEST + " : Last Comment 2"));
        caseCommentDAO.persist(createNewComment(person1, project2.getId(), null));

        Project project3 = new Project();

        project3.setName(REPORT_PROJECT_TEST + " : Test_Project 3");
        project3.setDescription(REPORT_PROJECT_TEST);
        project3.setStateId(PRESALE);
        project3.setCustomerType(En_CustomerType.STATE_BUDGET);
        project3.setCustomer(company2);
        project3.setTeam(Collections.singletonList(personProjectMemberView));

        project3.setId(projectService.createProject(getAuthToken(), project3).getData().getId());
        // no comments in project
    }

    @Test
    public void report() {
        init();

        ProjectQuery query = new ProjectQuery();
        query.setSearchString(REPORT_PROJECT_TEST);
        query.setStateIds(new HashSet<>(Collections.singletonList(FINISHED)));
        Report report = new Report();
        report.setQuery(serializeAsJson(query));
        report.setLocale("ru");

        boolean result = false;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            result = reportProject.writeReport(
                    buffer,
                    report,
                    deserializeFromJson(report.getQuery(), ProjectQuery.class),
                    id -> false
            );
        } catch (Exception exception) {
            Assert.fail();
        }

        Assert.assertTrue("writeReport success", result);
        Assert.assertTrue("report file is not empty", buffer.size() > 0);
    }

    @Test
    public void emptyReport() {
        init();

        ProjectQuery query = new ProjectQuery();
        query.setSearchString(REPORT_PROJECT_TEST);
        query.setStateIds(new HashSet<>(Collections.singletonList(UNKNOWN)));
        Report report = new Report();
        report.setQuery(serializeAsJson(query));
        report.setLocale("ru");

        boolean result = false;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            result = reportProject.writeReport(
                    buffer,
                    report,
                    deserializeFromJson(report.getQuery(), ProjectQuery.class),
                    id -> false
            );
        } catch (Exception exception) {
            Assert.fail();
        }

        Assert.assertTrue("writeReport success", result);
        Assert.assertTrue("report file is not empty", buffer.size() > 0);
    }

    @Test
    public void data() {
        init();

        ProjectQuery query = new ProjectQuery();
        query.setSearchString(REPORT_PROJECT_TEST);
        query.setStateIds(new HashSet<>(Collections.singletonList(PRESALE)));
        Report report = new Report();
        report.setQuery(serializeAsJson(query));
        report.setLocale("ru");

        List<ReportProjectWithComments> data = reportProject.createData(deserializeFromJson(report.getQuery(), ProjectQuery.class));

        Assert.assertEquals("Selected 2 cases", 2, data.size());
        Assert.assertEquals("name case #1", REPORT_PROJECT_TEST + " : Test_Project 1", data.get(0).getProject().getName());
        Assert.assertEquals("name case #2", REPORT_PROJECT_TEST + " : Test_Project 3", data.get(1).getProject().getName());

        Assert.assertEquals("comment text case #1", REPORT_PROJECT_TEST + " : Last Comment 1", data.get(0).getLastComment().getText());
        Assert.assertNull("comment case #2 is null", data.get(1).getLastComment());

        Assert.assertNull("period comment size case #1", data.get(0).getComments());
        Assert.assertNull("period comment size case #2", data.get(1).getComments());
    }

    @Test
    public void dataWithComments() {
        init();

        ProjectQuery query = new ProjectQuery();
        query.setSearchString(REPORT_PROJECT_TEST);
        query.setCommentCreationRange(new DateRange(En_DateIntervalType.TODAY, null, null));

        Report report = new Report();
        report.setQuery(serializeAsJson(query));
        report.setLocale("ru");

        List<ReportProjectWithComments> data = reportProject.createData(deserializeFromJson(report.getQuery(), ProjectQuery.class));

        Assert.assertEquals("Selected 2 cases", 2, data.size());
        Assert.assertEquals("name case #1", REPORT_PROJECT_TEST + " : Test_Project 1", data.get(0).getProject().getName());
        Assert.assertEquals("name case #2", REPORT_PROJECT_TEST + " : Test_Project 2", data.get(1).getProject().getName());

        Assert.assertEquals("comment text case #1", REPORT_PROJECT_TEST + " : Last Comment 1", data.get(0).getLastComment().getText());
        Assert.assertEquals("comment text case #2", REPORT_PROJECT_TEST + " : Last Comment 2", data.get(1).getLastComment().getText());

        Assert.assertEquals("period comment size case #1", 4, data.get(0).getComments().size());
        Assert.assertEquals("period comment size case #1", 3, data.get(1).getComments().size());
    }

    @Test
    public void emptyData() {
        init();

        ProjectQuery query = new ProjectQuery();
        query.setSearchString(REPORT_PROJECT_TEST);
        query.setStateIds(new HashSet<>(Collections.singletonList(UNKNOWN)));
        Report report = new Report();
        report.setQuery(serializeAsJson(query));
        report.setLocale("ru");

        List<ReportProjectWithComments> data = reportProject.createData(deserializeFromJson(report.getQuery(), ProjectQuery.class));

        Assert.assertEquals("no cases", 0, data.size());
    }

    @Autowired
    ReportProject reportProject;

    static private final String REPORT_PROJECT_TEST = "ReportProjectTest";
}
