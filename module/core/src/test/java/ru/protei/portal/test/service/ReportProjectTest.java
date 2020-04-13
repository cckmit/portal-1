package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.report.projects.ReportProject;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class, DatabaseConfiguration.class,
        IntegrationTestsConfiguration.class})
public class ReportProjectTest extends BaseServiceTest {

    @Before
    public void init() {
        Company company1 = createNewCompany("Test_Company 1", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(company1);
        Company company2 = createNewCompany("Test_Company 2", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(company2);

        Person person1 = createNewPerson(company1);
        personDAO.persist(person1);
        Person person2 = createNewPerson(company2);
        personDAO.persist(person2);

        Project project1 = new Project();

        project1.setName("Test_Project 1");
        project1.setDescription("ReportProjectTest");
        project1.setState(En_RegionState.PRESALE);
        project1.setCustomerType(En_CustomerType.STATE_BUDGET);
        project1.setCustomer(company1);
        project1.setTeam(new ArrayList<>());

        project1.setId(projectService.createProject(null, project1).getData().getId());

        caseCommentDAO.persist(createNewComment(person1, project1.getId(), "Comment"));
        caseCommentDAO.persist(createNewComment(person1, project1.getId(), "Last Comment"));

        Project project2 = new Project();

        project2.setName("Test_Project 2");
        project2.setDescription("ReportProjectTest");
        project2.setState(En_RegionState.FINISHED);
        project2.setCustomerType(En_CustomerType.STATE_BUDGET);
        project2.setCustomer(company1);
        project2.setTeam(new ArrayList<>());

        project2.setId(projectService.createProject(null, project2).getData().getId());

        caseCommentDAO.persist(createNewComment(person1, project2.getId(), "Comment"));
        caseCommentDAO.persist(createNewComment(person1, project2.getId(), "Last Comment"));

        Project project3 = new Project();

        project3.setName("Test_Project 3");
        project3.setDescription("ReportProjectTest");
        project3.setState(En_RegionState.PRESALE);
        project3.setCustomerType(En_CustomerType.STATE_BUDGET);
        project3.setCustomer(company2);
        project3.setTeam(new ArrayList<>());

        project3.setId(projectService.createProject(null, project3).getData().getId());
        // no comments in project
    }

    @Test
    public void processReport() {
        ProjectQuery query = new ProjectQuery();
        query.setStates(new HashSet<>(Collections.singletonList(En_RegionState.PRESALE)));
        Report report = new Report();
        report.setCaseQuery(query.toCaseQuery(1L));
        report.setLocale("ru");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            reportProject.writeReport(buffer, report);
        } catch (Exception exception) {
            Assert.fail();
        }

        Assert.assertTrue("report file is empty", buffer.size() > 0);
    }

    @Autowired
    ReportProject reportProject;
}
