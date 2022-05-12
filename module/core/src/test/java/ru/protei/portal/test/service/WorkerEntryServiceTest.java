package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JdbcConfigurationContext.class, DatabaseConfiguration.class,
        IntegrationTestsConfiguration.class, RemoteServiceFactory.class,
        HttpClientFactory.class, HttpConfigurationContext.class})
public class WorkerEntryServiceTest extends BaseServiceTest {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Test
    @Transactional
    public void testWorkerEntry() {
        Company homeCompany = companyDAO.get(1L);
        Person goodWorker = createNewPerson(homeCompany);
        Person badWorker = createNewPerson(homeCompany);
        personDAO.persistBatch(Arrays.asList(goodWorker, badWorker));

        CompanyHomeGroupItem companyGroupHome = new CompanyHomeGroupItem();
        companyGroupHome.setCompanyId(homeCompany.getId());
        companyGroupHome.setExternalCode(homeCompany.getCname());
        companyGroupHomeDAO.persist(companyGroupHome);

        CompanyDepartment companyDepartment = new CompanyDepartment();
        companyDepartment.setCompanyId(homeCompany.getId());
        companyDepartment.setCreated(new Date());
        companyDepartment.setName("Dep");
        companyDepartmentDAO.persist(companyDepartment);

        WorkerPosition workerPosition = new WorkerPosition();
        workerPosition.setCompanyId(homeCompany.getId());
        workerPosition.setName("QA");
        workerPositionDAO.persist(workerPosition);

        WorkerEntry goodWorkerEntry = new WorkerEntry();
        goodWorkerEntry.setCreated(new Date());
        goodWorkerEntry.setPersonId(goodWorker.getId());
        goodWorkerEntry.setExternalId("0000000001");
        goodWorkerEntry.setCompanyId(homeCompany.getId());
        goodWorkerEntry.setDepartmentId(companyDepartment.getId());
        goodWorkerEntry.setPositionId(workerPosition.getId());
        workerEntryDAO.persist(goodWorkerEntry);

        WorkerEntry badWorkerEntry = new WorkerEntry();
        badWorkerEntry.setCreated(new Date());
        badWorkerEntry.setPersonId(badWorker.getId());
        badWorkerEntry.setExternalId("0000000013");
        badWorkerEntry.setCompanyId(homeCompany.getId());
        badWorkerEntry.setDepartmentId(companyDepartment.getId());
        badWorkerEntry.setPositionId(workerPosition.getId());

        badWorkerEntry.setFiredDate(
                new GregorianCalendar(2021, Calendar.DECEMBER, 30, 0, 0, 0).getTime());
        workerEntryDAO.persist(badWorkerEntry);

        Person goodWorkerEntryDb = personDAO.get(goodWorker.getId());
        Assert.assertNotNull("Good Worker not created", goodWorkerEntryDb);
        Assert.assertFalse("Good Worker is not working", goodWorkerEntryDb.isFired());

        Person badWorkerEntryDb = personDAO.get(badWorker.getId());
        Assert.assertNotNull("Bad Worker not created", badWorkerEntryDb);
        Assert.assertFalse("Bad Worker is not working", badWorkerEntryDb.isFired());

        workerEntryService.updateFiredByDate(
                new GregorianCalendar(2021, Calendar.DECEMBER, 31, 0, 0, 0).getTime());

        Person nextDayGoodWorkerEntryDb = personDAO.get(goodWorker.getId());
        Assert.assertNotNull("Good Worker not created", nextDayGoodWorkerEntryDb);
        Assert.assertFalse("Good Worker is not working", nextDayGoodWorkerEntryDb.isFired());

        Person nextDayBadWorkerEntryDb = personDAO.get(badWorker.getId());
        Assert.assertNotNull("Bad Worker not created", nextDayBadWorkerEntryDb);
        Assert.assertTrue("Bad Worker is still working", nextDayBadWorkerEntryDb.isFired());
    }

    @Test
    @Transactional
    public void testWorkerUpdatePosition() {
        Company company = companyDAO.get(1L);
        Person person = createNewPerson(company);
        personDAO.persist(person);

        CompanyDepartment companyDepartment = createCompanyDepartmentRecord(company.getId(), "Company Department");
        Long companyDepartmentId = companyDepartmentDAO.persist(companyDepartment);
        Assert.assertNotNull(companyDepartmentId);

        CompanyDepartment newCompanyDepartment = createCompanyDepartmentRecord(company.getId(), "New Company Department");
        Long newCompanyDepartmentId = companyDepartmentDAO.persist(newCompanyDepartment);
        Assert.assertNotNull(newCompanyDepartmentId);

        WorkerPosition workerPosition = createWorkerPosition(company.getId());
        Long workerPositionId = workerPositionDAO.persist(workerPosition);
        Assert.assertNotNull(workerPositionId);

        WorkerEntry worker = createWorker(company.getId(), newCompanyDepartmentId, workerPosition, person.getId());
        Long workerId = workerEntryDAO.persist(worker);
        Assert.assertNotNull(workerId);

        workerEntryService.updatePositionByDate(new Date());
        WorkerEntry updatedWorker = workerEntryDAO.get(workerId);

        Assert.assertEquals(worker.getNewPositionName(), updatedWorker.getPositionName());
        Assert.assertNull(updatedWorker.getNewPositionName());
        Assert.assertNull(updatedWorker.getNewPositionDepartmentId());
        Assert.assertNull(updatedWorker.getNewPositionTransferDate());

        // check position not update
        worker = createWorker(company.getId(), companyDepartmentId, workerPosition, person.getId());
        worker.setNewPositionTransferDate(createTransferDate("01.01.2050"));
        workerId = workerEntryDAO.persist(worker);
        Assert.assertNotNull(workerId);

        workerEntryService.updatePositionByDate(new Date());
        updatedWorker = workerEntryDAO.get(workerId);

        Assert.assertNotNull(updatedWorker.getNewPositionName());
        Assert.assertNotNull(updatedWorker.getNewPositionDepartmentId());
        Assert.assertNotNull(updatedWorker.getNewPositionTransferDate());
    }

    private CompanyDepartment createCompanyDepartmentRecord(Long companyId, String name) {
        CompanyDepartment companyDepartment = new CompanyDepartment();
        companyDepartment.setCreated(new Date());
        companyDepartment.setName(name);
        companyDepartment.setCompanyId(companyId);
        return companyDepartment;
    }

    private WorkerPosition createWorkerPosition(Long companyId) {
        WorkerPosition workerPosition = new WorkerPosition();
        workerPosition.setName("Initial worker position");
        workerPosition.setCompanyId(companyId);
        return workerPosition;
    }

    private WorkerEntry createWorker(Long companyId, Long companyDepartmentId, WorkerPosition workerPosition, Long personId) {
        WorkerEntry worker = new WorkerEntry();
        worker.setPersonId(personId);
        Date now = new Date();
        worker.setCreated(now);
        worker.setCompanyId(companyId);
        worker.setDepartmentId(companyDepartmentId);
        worker.setPositionId(workerPosition.getId());
        worker.setPositionName(workerPosition.getName());
        worker.setNewPositionName("New worker position");
        worker.setNewPositionDepartmentId(companyDepartmentId);
        worker.setNewPositionTransferDate(createTransferDate("01.01.2022"));
        return worker;
    }

    private Date createTransferDate(String strDate) {
        LocalDate date = LocalDate.parse(strDate, formatter);
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
