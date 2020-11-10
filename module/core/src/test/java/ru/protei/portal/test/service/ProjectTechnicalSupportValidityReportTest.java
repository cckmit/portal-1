package ru.protei.portal.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.event.ExpiringProjectTSVNotificationEvent;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ProjectTSVReportInfo;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class, DatabaseConfiguration.class,
        IntegrationTestsConfiguration.class, ProjectTechnicalSupportValidityReportTest.TestConfiguration.class})
@Transactional
public class ProjectTechnicalSupportValidityReportTest extends BaseServiceTest {

    @Configuration
    static class TestConfiguration {
        static class EventPublisherServiceTest implements EventPublisherService {
            List<ApplicationEvent> applicationEvents = new ArrayList<>();

            public List<ApplicationEvent> getApplicationEvents() {
                List<ApplicationEvent> get = new ArrayList<>(applicationEvents);
                applicationEvents = new ArrayList<>();
                return get;
            }

            @Override
            public void publishEvent(ApplicationEvent event) {
                applicationEvents.add(event);
            }
        }

        @Bean
        public EventPublisherService getEventPublisherService() {
            return new EventPublisherServiceTest();
        }
    }


    private void init() {
        Company company1 = createNewCompany(TEST_ID + " : Test_Company 1", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(company1);
        Company company2 = createNewCompany(TEST_ID + " : Test_Company 2", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(company2);

        Person person1 = createNewPerson(company1);
        personDAO.persist(person1);
        Person person2 = createNewPerson(company2);
        personDAO.persist(person2);

        PersonProjectMemberView personProjectMemberView =
                new PersonProjectMemberView(person1.getDisplayShortName(), person1.getId(), person1.isFired(), En_DevUnitPersonRoleType.HEAD_MANAGER);

        Project project1 = new Project();

        project1.setName(TEST_ID + " : Test_Project 1");
        project1.setDescription(TEST_ID);
        project1.setState(En_RegionState.PRESALE);
        project1.setCustomerType(En_CustomerType.STATE_BUDGET);
        project1.setCustomer(company1);
        project1.setTeam(Collections.singletonList(personProjectMemberView));
        project1.setTechnicalSupportValidity(date7_11152020_0000);

        project1.setId(projectService.createProject(getAuthToken(), project1).getData().getId());

        Project project2 = new Project();

        project2.setName(TEST_ID + " : Test_Project 2");
        project2.setDescription(TEST_ID);
        project2.setState(En_RegionState.FINISHED);
        project2.setCustomerType(En_CustomerType.STATE_BUDGET);
        project2.setCustomer(company1);
        project2.setTeam(Collections.singletonList(personProjectMemberView));

        project2.setId(projectService.createProject(getAuthToken(), project2).getData().getId());

        Project project3 = new Project();

        project3.setName(TEST_ID + " : Test_Project 3");
        project3.setDescription(TEST_ID);
        project3.setState(En_RegionState.PRESALE);
        project3.setCustomerType(En_CustomerType.STATE_BUDGET);
        project3.setCustomer(company2);
        project3.setTeam(Collections.singletonList(personProjectMemberView));
        project3.setTechnicalSupportValidity(date30_12052020_0000);

        project3.setId(projectService.createProject(getAuthToken(), project3).getData().getId());
    }

    @Test
    @Transactional
    public void report() {
        init();
        projectService.notifyExpiringProjectTechnicalSupportValidity(dateNow_11102020_1000);

        TestConfiguration.EventPublisherServiceTest publisherServiceTest = (TestConfiguration.EventPublisherServiceTest)publisherService;
        final List<ExpiringProjectTSVNotificationEvent> notificationEvents = publisherServiceTest.getApplicationEvents().stream()
                .filter(event -> event instanceof ExpiringProjectTSVNotificationEvent)
                .map(event -> (ExpiringProjectTSVNotificationEvent) event)
                .collect(Collectors.toList());
        assertEquals(1, notificationEvents.size());

        ExpiringProjectTSVNotificationEvent event = notificationEvents.get(0);
        final List<ProjectTSVReportInfo> projectIn7days = event.getInfos().get(En_ExpiringProjectTSVPeriod.DAYS_7);
        assertEquals(1, projectIn7days.size());
        assertEquals(TEST_ID + " : Test_Company 1", projectIn7days.get(0).getCustomerName());

        final List<ProjectTSVReportInfo> projectIn30days = event.getInfos().get(En_ExpiringProjectTSVPeriod.DAYS_30);
        assertEquals(1, projectIn30days.size());
        assertEquals(TEST_ID + " : Test_Company 2", projectIn30days.get(0).getCustomerName());
    }

    @Test
    @Transactional
    public void emptyReport() {
        projectService.notifyExpiringProjectTechnicalSupportValidity(dateNow_11102020_1000);

        TestConfiguration.EventPublisherServiceTest publisherServiceTest = (TestConfiguration.EventPublisherServiceTest)publisherService;
        final List<ExpiringProjectTSVNotificationEvent> notificationEvents = publisherServiceTest. getApplicationEvents().stream()
                .filter(event -> event instanceof ExpiringProjectTSVNotificationEvent)
                .map(event -> (ExpiringProjectTSVNotificationEvent) event)
                .collect(Collectors.toList());
        assertEquals(0, notificationEvents.size());
    }

    @Autowired
    EventPublisherService publisherService;

    private Date dateNow_11102020_1000 = new Date(1605002400000L);  // 11/10/2020 @ 10:00am (UTC)
    private Date date7_11152020_0000 = new Date(1605398400000L);    // 11/15/2020 @ 12:00am (UTC)
    private Date date30_12052020_0000 = new Date(1607126400000L);   // 12/05/2020 @ 12:00am (UTC)

    static private final String TEST_ID = "ProjectTechnicalSupportValidityReportTest";
}
