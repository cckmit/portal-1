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

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

import static ru.protei.portal.core.model.util.CrmConstants.State.*;

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
        Company companyLess7 = createNewCompany(TEST_ID + " : Test_Company Less7", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(companyLess7);
        Company company7 = createNewCompany(TEST_ID + " : Test_Company 7", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(company7);
        Company companyBetween7And14 = createNewCompany(TEST_ID + " : Test_Company Between7And14", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(companyBetween7And14);
        Company company14 = createNewCompany(TEST_ID + " : Test_Company 14", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(company14);
        Company companyBetween14And30 = createNewCompany(TEST_ID + " : Test_Company Between14And30", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(companyBetween14And30);
        Company company30 = createNewCompany(TEST_ID + " : Test_Company 30", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(company30);
        Company companyMore30 = createNewCompany(TEST_ID + " : Test_Company More 30", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(companyMore30);
        Company companyNo = createNewCompany(TEST_ID + " : Test_Company No", En_CompanyCategory.CUSTOMER);
        companyDAO.persist(companyNo);

        Person personLess7 = createNewPerson(companyLess7);
        personDAO.persist(personLess7);
        Person person = createNewPerson(company7);
        personDAO.persist(person);
        Person personBetween7and14 = createNewPerson(companyBetween7And14);
        personDAO.persist(personBetween7and14);
        Person personBetween14And30 = createNewPerson(companyBetween14And30);
        personDAO.persist(personBetween14And30);
        Person personMore30 = createNewPerson(companyMore30);
        personDAO.persist(personMore30);
        Person personNo = createNewPerson(companyNo);
        personDAO.persist(personNo);

        PersonProjectMemberView headManagerLess7 =
                new PersonProjectMemberView(personLess7.getDisplayShortName(), personLess7.getId(), personLess7.isFired(), En_PersonRoleType.HEAD_MANAGER);
        PersonProjectMemberView headManager =
                new PersonProjectMemberView(person.getDisplayShortName(), person.getId(), person.isFired(), En_PersonRoleType.HEAD_MANAGER);
        PersonProjectMemberView headManagerBetween7And14 =
                new PersonProjectMemberView(personBetween7and14.getDisplayShortName(), personBetween7and14.getId(), personBetween7and14.isFired(), En_PersonRoleType.HEAD_MANAGER);
        PersonProjectMemberView headManagerBetween14And30 =
                new PersonProjectMemberView(personBetween14And30.getDisplayShortName(), personBetween14And30.getId(), personBetween14And30.isFired(), En_PersonRoleType.HEAD_MANAGER);
        PersonProjectMemberView headManagerMore30 =
                new PersonProjectMemberView(personMore30.getDisplayShortName(), personMore30.getId(), personMore30.isFired(), En_PersonRoleType.HEAD_MANAGER);
        PersonProjectMemberView headManagerNo =
                new PersonProjectMemberView(personNo.getDisplayShortName(), personNo.getId(), personNo.isFired(), En_PersonRoleType.HEAD_MANAGER);

        Project projectLess7 = new Project();

        projectLess7.setName(TEST_ID + " : Test_Project Less7");
        projectLess7.setDescription(TEST_ID);
        projectLess7.setStateId(PRESALE);
        projectLess7.setCustomerType(En_CustomerType.STATE_BUDGET);
        projectLess7.setCustomer(companyLess7);
        projectLess7.setTeam(Collections.singletonList(headManagerLess7));
        projectLess7.setTechnicalSupportValidity(dateLess7);
        projectLess7.setId(projectService.createProject(getAuthToken(), projectLess7).getData().getId());

        Project project7 = new Project();

        project7.setName(TEST_ID + " : Test_Project 7");
        project7.setDescription(TEST_ID);
        project7.setStateId(PRESALE);
        project7.setCustomerType(En_CustomerType.STATE_BUDGET);
        project7.setCustomer(company7);
        project7.setTeam(Collections.singletonList(headManager));
        project7.setTechnicalSupportValidity(date7);

        project7.setId(projectService.createProject(getAuthToken(), project7).getData().getId());

        Project projectBetween7And14 = new Project();

        projectBetween7And14.setName(TEST_ID + " : Test_Project Between7And14");
        projectBetween7And14.setDescription(TEST_ID);
        projectBetween7And14.setStateId(PRESALE);
        projectBetween7And14.setCustomerType(En_CustomerType.STATE_BUDGET);
        projectBetween7And14.setCustomer(companyBetween7And14);
        projectBetween7And14.setTeam(Collections.singletonList(headManagerBetween7And14));
        projectBetween7And14.setTechnicalSupportValidity(dateBetween7And14);

        projectBetween7And14.setId(projectService.createProject(getAuthToken(), projectBetween7And14).getData().getId());

        Project project14 = new Project();

        project14.setName(TEST_ID + " : Test_Project 14");
        project14.setDescription(TEST_ID);
        project14.setStateId(FINISHED);
        project14.setCustomerType(En_CustomerType.STATE_BUDGET);
        project14.setCustomer(company14);
        project14.setTeam(Collections.singletonList(headManager));
        project14.setTechnicalSupportValidity(date14);

        project14.setId(projectService.createProject(getAuthToken(), project14).getData().getId());

        Project projectBetween14And30 = new Project();

        projectBetween14And30.setName(TEST_ID + " : Test_Project Between14And30");
        projectBetween14And30.setDescription(TEST_ID);
        projectBetween14And30.setStateId(FINISHED);
        projectBetween14And30.setCustomerType(En_CustomerType.STATE_BUDGET);
        projectBetween14And30.setCustomer(companyBetween14And30);
        projectBetween14And30.setTeam(Collections.singletonList(headManagerBetween14And30));
        projectBetween14And30.setTechnicalSupportValidity(dateBetween14And30);

        projectBetween14And30.setId(projectService.createProject(getAuthToken(), projectBetween14And30).getData().getId());

        Project project30 = new Project();

        project30.setName(TEST_ID + " : Test_Project 30");
        project30.setDescription(TEST_ID);
        project30.setStateId(PRESALE);
        project30.setCustomerType(En_CustomerType.STATE_BUDGET);
        project30.setCustomer(company30);
        project30.setTeam(Collections.singletonList(headManager));
        project30.setTechnicalSupportValidity(date30);

        project30.setId(projectService.createProject(getAuthToken(), project30).getData().getId());

        Project projectMore30 = new Project();

        projectMore30.setName(TEST_ID + " : Test_Project more 30");
        projectMore30.setDescription(TEST_ID);
        projectMore30.setStateId(PRESALE);
        projectMore30.setCustomerType(En_CustomerType.STATE_BUDGET);
        projectMore30.setCustomer(companyMore30);
        projectMore30.setTeam(Collections.singletonList(headManagerMore30));
        projectMore30.setTechnicalSupportValidity(dateMore30);

        projectMore30.setId(projectService.createProject(getAuthToken(), projectMore30).getData().getId());

        Project projectNo = new Project();

        projectNo.setName(TEST_ID + " : Test_Project NO");
        projectNo.setDescription(TEST_ID);
        projectNo.setStateId(PRESALE);
        projectNo.setCustomerType(En_CustomerType.STATE_BUDGET);
        projectNo.setCustomer(companyNo);
        projectNo.setTeam(Collections.singletonList(headManagerNo));

        projectNo.setId(projectService.createProject(getAuthToken(), projectNo).getData().getId());
    }

    @Test
    @Transactional
    public void report() {
        init();
        projectService.notifyExpiringProjectTechnicalSupportValidity(dateNow);

        TestConfiguration.EventPublisherServiceTest publisherServiceTest = (TestConfiguration.EventPublisherServiceTest)publisherService;
        final List<ExpiringProjectTSVNotificationEvent> notificationEvents = publisherServiceTest.getApplicationEvents().stream()
                .filter(event -> event instanceof ExpiringProjectTSVNotificationEvent)
                .map(event -> (ExpiringProjectTSVNotificationEvent) event)
                .collect(Collectors.toList());
        assertEquals(1, notificationEvents.size());

        ExpiringProjectTSVNotificationEvent event = notificationEvents.get(0);
        final List<ProjectTSVReportInfo> projectIn7days = event.getInfos().get(En_ExpiringProjectTSVPeriod.DAYS_7);
        assertEquals(1, projectIn7days.size());
        assertEquals(TEST_ID + " : Test_Company 7", projectIn7days.get(0).getCustomerName());

        final List<ProjectTSVReportInfo> projectIn14days = event.getInfos().get(En_ExpiringProjectTSVPeriod.DAYS_14);
        assertEquals(1, projectIn14days.size());
        assertEquals(TEST_ID + " : Test_Company 14", projectIn14days.get(0).getCustomerName());

        final List<ProjectTSVReportInfo> projectIn30days = event.getInfos().get(En_ExpiringProjectTSVPeriod.DAYS_30);
        assertEquals(1, projectIn30days.size());
        assertEquals(TEST_ID + " : Test_Company 30", projectIn30days.get(0).getCustomerName());
    }

    @Test
    @Transactional
    public void emptyReport() {
        projectService.notifyExpiringProjectTechnicalSupportValidity(dateNow);

        TestConfiguration.EventPublisherServiceTest publisherServiceTest = (TestConfiguration.EventPublisherServiceTest)publisherService;
        final List<ExpiringProjectTSVNotificationEvent> notificationEvents = publisherServiceTest. getApplicationEvents().stream()
                .filter(event -> event instanceof ExpiringProjectTSVNotificationEvent)
                .map(event -> (ExpiringProjectTSVNotificationEvent) event)
                .collect(Collectors.toList());
        assertEquals(0, notificationEvents.size());
    }

    @Autowired
    EventPublisherService publisherService;

    private LocalDate dateNow = LocalDate.of(2020, Month.NOVEMBER, 10);            // 11/10/2020 @ 12:00am (UTC)
    private Date dateLess7 = new Date(1605484800000L);          // 11/16/2020 @ 12:00am (UTC)
    private Date date7 = new Date(1605571200000L);              // 11/17/2020 @ 12:00am (UTC)
    private Date dateBetween7And14 = new Date(1605830400000L);  // 11/20/2020 @ 12:00am (UTC)
    private Date date14 = new Date(1606176000000L);             // 11/24/2020 @ 12:00am (UTC)
    private Date dateBetween14And30 = new Date(1606780800000L); // 12/01/2020 @ 12:00am (UTC)
    private Date date30 = new Date(1607558400000L);             // 12/10/2020 @ 12:00am (UTC)
    private Date dateMore30 = new Date(1607644800000L);         // 12/11/2020 @ 12:00am (UTC)

    static private final String TEST_ID = "ProjectTechnicalSupportValidityReportTest";
}