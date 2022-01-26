package ru.protei.portal.test.api;

import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.config.APIConfigurationContext;
import ru.protei.portal.api.config.WSConfig;
import ru.protei.portal.api.model.*;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.struct.Photo;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {APIConfigurationContext.class, DatabaseConfiguration.class})
public class TestWorkerController {

    @Autowired
    WebApplicationContext webApplicationContext;

    private static Logger logger = LoggerFactory.getLogger(TestWorkerController.class);
    private MockMvc mockMvc;
    private static String BASE_URI;
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;
    private UserRoleDAO userRoleDAO;
    private UserLoginDAO userLoginDAO;
    private PersonDAO personDAO;
    private WorkerEntryDAO workerEntryDAO;
    private CompanyDepartmentDAO companyDepartmentDAO;
    private WorkerPositionDAO workerPositionDAO;
    private CompanyDAO companyDAO;
    private String WS_API_TEST_ROLE_CODE = "ws_api_test_role" + System.currentTimeMillis();
    private String QWERTY_PASSWORD = "qwerty_test_API" + new Date().getTime();
    private UserRole userRole;
    private Person person;
    private String FIRST_PHOTO = "test1.jpg";
    private String SECOND_PHOTO = "test2.jpg";

    @BeforeClass
    public static void initClass() throws Exception {
        initJAXB();
        BASE_URI = "http://localhost:8090/api/worker/";
    }

    @Before
    public void createPersonToAuth() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        personDAO = webApplicationContext.getBean(PersonDAO.class);
        userLoginDAO = webApplicationContext.getBean(UserLoginDAO.class);
        userRoleDAO = webApplicationContext.getBean(UserRoleDAO.class);
        workerEntryDAO = webApplicationContext.getBean(WorkerEntryDAO.class);
        companyDepartmentDAO = webApplicationContext.getBean(CompanyDepartmentDAO.class);
        workerPositionDAO = webApplicationContext.getBean(WorkerPositionDAO.class);
        companyDAO = webApplicationContext.getBean(CompanyDAO.class);

        createAndPersistPerson();
        createAndPersistUserRoles();
        createAndPersistUserLogin();
    }

    @After
    public void removePersonToAuth() {
        if (person != null) {
            removeUserLogin();
            removePerson();
        }

        removeUserRoles();

        workerEntryDAO.removeAll();
    }

    @Test
    public void testAddWorker() throws Exception {
        WorkerRecord worker = createWorkerRecord();
        Result result;
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);

        result = addWorker(new WorkerRecord());
        Assert.assertEquals("add.worker: empty worker was added! ", true, result.isError());

        worker.setFireDate("2019-05-05");
        result = addWorker(worker);
        Assert.assertEquals("add.worker: fired worker was added! ", true, result.isError());

        worker.setFireDate(null);
        worker.setFired(false);
        result = addWorker(worker);
        Assert.assertEquals("add.worker is not success! " + result.getMessage(), true, result.isOk());

        result = addWorker(worker);
        Assert.assertEquals("add.worker: already exist worker was added! " + result.getMessage(), true, result.isError());
    }


    @Test
    public void testUpdateWorker() throws Exception {
        WorkerRecord worker = createWorkerRecord();
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        Result<Long> successResult = addWorker(worker);
        Result result;

        result = updateWorker(worker);
        Assert.assertEquals("update.worker worker with nonexistent personId was updated!", true, result.isError());

        worker.setId(successResult.getData());
        result = updateWorker(worker);
        Assert.assertEquals("update.worker is not success! " + result.getMessage(), true, result.isOk());

        worker.setFireDate("2019-05-05");
        result = updateWorker(worker);
        Assert.assertEquals("update.worker is not success! " + result.getMessage(), true, result.isOk());

        result = updateWorker(worker);
        Assert.assertEquals("update.worker fired worker was updated!", true, result.isError());
    }

    @Test
    public void testUpdateFireDate() throws Exception {
        WorkerRecord worker = createWorkerRecord();
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        Result<Long> addResult = addWorker(worker);
        Result result;

        String uri = BASE_URI + "get.person";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri).queryParam("id", addResult.getData());
        String uriBuilder = builder.build().toUriString();

        WorkerRecord resultWorker;

        worker.setFireDate("");
        worker.setId(null);
        result = updateFireDate(worker);
        resultWorker = getWorkerByUri(uriBuilder).getData();

        Assert.assertEquals("update.fire.date is not success! " + result.getMessage(), true, result.isOk());
        Assert.assertEquals("update.fire.date: fire date are changed!", null, resultWorker.getFireDate());

        WorkerRecord secondWorker = createWorkerRecord();
        secondWorker.setFirstName("111");
        secondWorker.setFireDate("2019-05-05");
        secondWorker.setId(null);
        result = updateFireDate(secondWorker);

        Assert.assertEquals("update.fire.date is not success! " + result.getMessage(), true, result.isOk());

        worker.setId(addResult.getData());

        worker.setFireDate("2019-05-05");
        updateFireDate(worker);
        resultWorker = getWorkerByUri(uriBuilder).getData();
        Assert.assertEquals("update.fire.date: the fire date is saved when isFired = false in database!", null, resultWorker.getFireDate());
        Assert.assertEquals("update.fire.date: is fired became true when fire-date is not null", false, resultWorker.isFired());


        worker.setFireDate(null);
        worker.setFired(true);
        result = updateWorker(worker);
        Assert.assertEquals("update.fire.date is not success! " + result.getMessage(), true, result.isOk());

        resultWorker = getWorkerByUri(uriBuilder).getData();

        Assert.assertEquals("update.fire.date: worker is not fired!", worker.isFired(), resultWorker.isFired());

        worker.setFireDate("2019-05-05");
        result = updateFireDate(worker);
        Assert.assertEquals("update.fire.date is not success! " + result.getMessage(), true, result.isOk());

        resultWorker = getWorkerByUri(uriBuilder).getData();

        Assert.assertEquals("update.fire.date: fire date are different!", worker.getFireDate(), resultWorker.getFireDate());

        worker.setFireDate("2018-05-05");
        updateFireDate(worker);
        resultWorker = getWorkerByUri(uriBuilder).getData();

        Assert.assertNotEquals("update.fire.date: earlier fire date was set!", worker.getFireDate(), resultWorker.getFireDate());

        worker.setFireDate("2019-06-06");
        updateFireDate(worker);
        resultWorker = getWorkerByUri(uriBuilder).getData();

        Assert.assertEquals("update.fire.date: fire date are different!", worker.getFireDate(), resultWorker.getFireDate());

        worker.setFireDate("2019-06-07");
        worker.setId(null);
        updateFireDate(worker);
        resultWorker = getWorkerByUri(uriBuilder).getData();

        Assert.assertEquals("update.fire.date: fire date are different!", worker.getFireDate(), resultWorker.getFireDate());
    }


    @Test
    public void testUpdateFireDates() throws Exception {
        WorkerRecord firstWorker = createWorkerRecord();
        WorkerRecord secondWorker = createWorkerRecord();
        WorkerRecord thirdWorker = createWorkerRecord();
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        Result result = addWorker(firstWorker);
        addWorker(secondWorker);
        addWorker(thirdWorker);

        firstWorker.setId((Long)result.getData());

        firstWorker.setFired(true);
        result = updateWorker(firstWorker);
        Assert.assertEquals("update.fire.date is not success! " + result.getMessage(), true, result.isOk());

        secondWorker.setFireDate("2019-05-05");
        firstWorker.setFireDate("2019-06-06");

        WorkerRecordList list = new WorkerRecordList();
        list.append(firstWorker);
        list.append(secondWorker);
        list.append(thirdWorker);

        ResultList resultList = updateFireDates(list);

        for (Result res : resultList.getResults()) {
            Assert.assertEquals("update.fire.date is not success! " + res.getMessage(), true, res.isOk());
        }
    }


    @Test
    public void testDeleteWorker() throws Exception {
        WorkerRecord worker = createWorkerRecord();
        Result result;
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);

        addWorker(worker);
        result = deleteWorker(worker);
        Assert.assertEquals("delete.worker is not success! " + result.getMessage(), true, result.isOk());

        result = deleteWorker(worker);
        Assert.assertEquals("delete.worker: already deleted worker was deleted! ", true, result.isError());
    }

    @Test
    public void testGetPerson() throws Exception {
        String uri = BASE_URI + "get.person";

        WorkerRecord worker = createWorkerRecord();
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        Result<Long> result = addWorker(worker);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri).queryParam("id", result.getData());
        String uriBuilder = builder.build().toUriString();

        WorkerRecord wr = getWorkerByUri(uriBuilder).getData();

        Assert.assertEquals("get.person: added and got person are different", result.getData(), wr.getId());
    }

    @Test
    public void testGetWorker() throws Exception {
        String uri = BASE_URI + "get.worker";
        WorkerRecord worker = createWorkerRecord();
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        addWorker(worker);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("id", worker.getWorkerId())
                .queryParam("companyCode", worker.getCompanyCode());

        String uriBuilder = builder.build().toUriString();

        WorkerRecord wr = getWorkerByUri(uriBuilder).getData();

        Assert.assertEquals("get.worker: added and got worker are different", worker.getWorkerId(), wr.getWorkerId());
    }

    @Test
    public void testGetDepartment() throws Exception {
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);

        DepartmentRecord dr = getDepartment(department).getData();

        Assert.assertEquals("get.department: added and got worker are different", department.getDepartmentId(), dr.getDepartmentId());
    }

    @Test
    public void testUpdateDepartment() throws Exception {
        DepartmentRecord department = createDepartmentRecord();

        Result result = createOrUpdateDepartment(department);

        Assert.assertEquals("update.department is not success! " + result.getMessage(), true, result.isOk());
    }

    @Test
    public void testDeleteDepartment() throws Exception {
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);

        Result result = deleteDepartment(department);

        Assert.assertEquals("delete.department is not success! " + result.getMessage(), true, result.isOk());
    }


    @Test
    public void testUpdatePosition() throws Exception {
        String uri = BASE_URI + "update.position";
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        WorkerRecord worker = createWorkerRecord();
        addWorker(worker);
        String newPosition = "Test position " + System.currentTimeMillis();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("oldName", worker.getPositionName())
                .queryParam("newName", newPosition)
                .queryParam("companyCode", worker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        logger.debug("result URI = " + uriBuilder);

        ResultActions resultActions = mockMvc.perform(
                put(uriBuilder)
                        .header("Accept", "application/xml")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
        );
        Result result = (Result) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("result = " + result);

        Assert.assertEquals("update.position is not success! " + result.getMessage(), true, result.isOk());
    }

    @Test
    public void testDeletePosition() throws Exception {
        String uri = BASE_URI + "delete.position";
        WorkerRecord worker = createWorkerRecord();
        worker.setPositionName("Unique position for delete test");
        addWorker(worker);
        deleteWorker(worker);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("name", worker.getPositionName())
                .queryParam("companyCode", worker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        logger.debug("result URI = " + uriBuilder);

        ResultActions resultActions = mockMvc.perform(
                delete(uriBuilder)
                        .header("Accept", "application/xml")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
        );
        Result result = (Result) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("result = " + result);

        Assert.assertEquals("delete.position is not success! " + result.getMessage(), true, result.isOk());
    }

    @Test
    public void testGetPhoto() throws Exception {
        Long id = 1L;

        createPhotosById(id);

        String uri = BASE_URI + "get.photo/" + id;

        logger.debug("URI = " + uri);

        ResultActions result = mockMvc.perform(
                get(uri)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .header("Accept", MediaType.IMAGE_JPEG)
        );

        Assert.assertEquals("Request status is not OK", HttpServletResponse.SC_OK, result.andReturn().getResponse().getStatus());

        String sourcePhotoName = WSConfig.getInstance().getDirPhotos() + id + ".jpg";
        Path sourcePhotoPath = Paths.get(sourcePhotoName);

        String diffPhotoName = WSConfig.getInstance().getDirPhotos() + SECOND_PHOTO;
        Path diffPhotoPath = Paths.get(diffPhotoName);

        byte[] receivedPhotoBytes = result.andReturn().getResponse().getContentAsByteArray();

        Assert.assertTrue("Sent photo and received photo are not equals!", Arrays.equals(receivedPhotoBytes, Files.readAllBytes(sourcePhotoPath)));
        Assert.assertFalse("Received photo are equal to different photo", Arrays.equals(receivedPhotoBytes, Files.readAllBytes(diffPhotoPath)));

        Files.deleteIfExists(sourcePhotoPath);
    }

    @Test
    public void testUpdatePhoto() throws Exception {
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        WorkerRecord worker = createWorkerRecord();
        Result<Long> result = addWorker(worker);

        Long id = result.getData();

        createPhotosById(id);

        String photoByIdName = WSConfig.getInstance().getDirPhotos() + id + ".jpg";
        String photoToUpdateName = WSConfig.getInstance().getDirPhotos() + SECOND_PHOTO;

        Assert.assertFalse("Оld and new photo should be different", Arrays.equals(Files.readAllBytes(Paths.get(photoToUpdateName)), Files.readAllBytes(Paths.get(photoByIdName))));

        String uri = BASE_URI + "update.photo/" + id;

        logger.debug("URI = " + uri);

        ResultActions resultActions = mockMvc.perform(
                put(uri)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.IMAGE_JPEG)
                        .content(Files.readAllBytes(Paths.get(photoToUpdateName)))
        );

        Assert.assertEquals("Request status is not OK", HttpServletResponse.SC_OK, resultActions.andReturn().getResponse().getStatus());

        Assert.assertTrue("Updated and new photo should be equals", Arrays.equals(Files.readAllBytes(Paths.get(photoToUpdateName)), Files.readAllBytes(Paths.get(photoByIdName))));

        Files.deleteIfExists(Paths.get(photoByIdName));
    }

    @Test
    public void testUpdateWorkerPosition() throws Exception {
        Company company = companyDAO.getCompanyByName("Протей");
        CompanyDepartment companyDepartment = createCompanyDepartment(company.getId(), "Company Department");
        Long companyDepartmentId = companyDepartmentDAO.persist(companyDepartment);
        Assert.assertNotNull(companyDepartmentId);

        WorkerPosition workerPosition = createInitialWorkerPosition(company.getId());
        Long workerPositionId = workerPositionDAO.persist(workerPosition);
        Assert.assertNotNull(workerPositionId);

        WorkerEntry worker = createWorker(company.getId(), companyDepartmentId, workerPosition);
        Long workerId = workerEntryDAO.persist(worker);
        Assert.assertNotNull(workerId);

        CompanyDepartment newCompanyDepartment = createCompanyDepartment(company.getId(), "New Company Department");
        Long newCompanyDepartmentId = companyDepartmentDAO.persist(newCompanyDepartment);
        Assert.assertNotNull(companyDepartmentId);

        WorkerRecord newWorkerPosition = createNewWorkerPosition(worker.getExternalId(), newCompanyDepartmentId);
        Result<Long> updatedWorkerResult = updateWorkerPosition(newWorkerPosition);
        Assert.assertTrue(updatedWorkerResult.isOk());

        WorkerEntry updatedWorker = workerEntryDAO.get(updatedWorkerResult.getData());
        Assert.assertNotNull(updatedWorker);
        Assert.assertEquals(newWorkerPosition.getNewPositionName(), updatedWorker.getPositionName());
        Assert.assertEquals(newWorkerPosition.getNewPositionDepartmentId(), updatedWorker.getDepartmentId());
    }

    private void createPhotosById(Long id) throws Exception{
            String photoByIdName = WSConfig.getInstance().getDirPhotos() + id + ".jpg";
            Path photoByIdPath = Paths.get(photoByIdName);

            String existPhotoName = WSConfig.getInstance().getDirPhotos() + FIRST_PHOTO;
            Path existPhotoPath = Paths.get(existPhotoName);

            Files.deleteIfExists(photoByIdPath);
            Files.copy(existPhotoPath, photoByIdPath);
    }


    private WorkerRecord createWorkerRecord() {
        WorkerRecord worker = new WorkerRecord();

        worker.setCompanyCode("protei");
        worker.setFirstName("TestFirstName" + System.currentTimeMillis());
        worker.setLastName("TestLastName" + System.currentTimeMillis());
        worker.setSecondName("TestSecondName" + System.currentTimeMillis());
        worker.setSex(2);
        worker.setBirthday("1998-07-15");
        worker.setPhoneWork(String.valueOf(new Random().nextInt(899) + 100));
        worker.setPhoneHome("999-55-55");
        worker.setPhoneMobile("+79610000000");
        worker.setEmail(System.currentTimeMillis() + "test3_up@protei.ru");
        worker.setEmailOwn(System.currentTimeMillis() + "test3@protei.ru");
        worker.setFax(worker.getPhoneWork());
        worker.setAddress("test address 3 up");
        worker.setAddressHome("test address 3");
        worker.setPassportInfo("test passport info 3 up");
        worker.setInfo("test info 3 up");
        worker.setIpAddress("192.168.100." + new Random().nextInt(255));
        worker.setDeleted(new Boolean(false));
        worker.setWorkerId(String.valueOf(System.currentTimeMillis()));
        worker.setDepartmentId("111111111");
        worker.setHireDate("2015-06-05");
        worker.setHireOrderNo("Order N: " + System.currentTimeMillis());
        worker.setActive(1);
        worker.setPositionName("Test position");

        logger.debug("worker = " + worker);

        return worker;
    }

    private DepartmentRecord createDepartmentRecord() {
        DepartmentRecord department = new DepartmentRecord();

        department.setCompanyCode("protei");
        department.setDepartmentId("111111111");
        department.setDepartmentName("TestDepartment1");

        logger.debug("department = " + department);

        return department;
    }

    private static void initJAXB() throws Exception {
        JAXBContext context = JAXBContext.newInstance(WorkerRecord.class, WorkerRecordList.class,
                DepartmentRecord.class, IdList.class,
                Photo.class, PhotoList.class,
                Result.class, ResultList.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        unmarshaller = context.createUnmarshaller();
    }

    private Result<Long> addWorker(WorkerRecord worker) throws Exception {
        logger.debug("worker input = " + worker);

        String uri = BASE_URI + "add.worker";

        String workerXml = toXml(worker);

        ResultActions resultActions = mockMvc.perform(
                post(uri)
                        .header("Accept", MediaType.APPLICATION_XML)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
                        .content(workerXml)
        );
        Result<Long> result = (Result<Long>) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("result = " + result);

        return result;
    }


    private Result updateWorker(WorkerRecord worker) throws Exception {
        logger.debug("worker input = " + worker);

        String uri = BASE_URI + "update.worker";

        String workerXml = toXml(worker);

        ResultActions resultActions = mockMvc.perform(
                put(uri)
                        .header("Accept", MediaType.APPLICATION_XML)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
                        .content(workerXml)
        );
        Result result = (Result) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("result = " + result);

        return result;
    }

    private Result updateFireDate (WorkerRecord worker) throws Exception {
        logger.debug("worker input = " + worker);

        String uri = BASE_URI + "update.fire.date";

        String workerXml = toXml(worker);

        ResultActions resultActions = mockMvc.perform(
                put(uri)
                        .header("Accept", MediaType.APPLICATION_XML)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
                        .content(workerXml)
        );
        Result result = (Result) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("result = " + result);

        return result;
    }

    private ResultList updateFireDates (WorkerRecordList list) throws Exception {
        logger.debug("worker input = " + list);

        String uri = BASE_URI + "update.fire.dates";

        String listXml = toXml(list);

        ResultActions resultActions = mockMvc.perform(
                put(uri)
                        .header("Accept", MediaType.APPLICATION_XML)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
                        .content(listXml)
        );

        ResultList resultList = (ResultList) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("resultList = " + resultList);

        return resultList;
    }


    private Result deleteWorker(WorkerRecord worker) throws Exception {
        String uri = BASE_URI + "delete.worker";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("externalId", worker.getWorkerId())
                .queryParam("companyCode", worker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        logger.debug("result URI = " + uriBuilder);

        ResultActions resultActions = mockMvc.perform(
                delete(uriBuilder)
                        .header("Accept", MediaType.APPLICATION_XML)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
        );

        Result result = (Result) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("result = " + result);

        return result;
    }

    private Result<WorkerRecord> getWorkerByUri(String uri) throws Exception {
        logger.debug("result URI = " + uri);

        ResultActions result = mockMvc.perform(
                get(uri)
                        .header("Accept", MediaType.APPLICATION_XML)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
        );

        Result<WorkerRecord> workerRecord = (Result<WorkerRecord>) fromXml(result.andReturn().getResponse().getContentAsString());

        logger.debug("WorkerRecord = " + workerRecord);

        return workerRecord;
    }

    private Result createOrUpdateDepartment(DepartmentRecord department) throws Exception {
        logger.debug("department input = " + department);

        String uri = BASE_URI + "update.department";

        String departmentXml = toXml(department);

        ResultActions resultActions = mockMvc.perform(
                put(uri)
                        .header("Accept", MediaType.APPLICATION_XML)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
                        .content(departmentXml)
        );

        Result result = (Result) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("result = " + result);

        return result;
    }

    private Result deleteDepartment(DepartmentRecord department) throws Exception {
        String uri = BASE_URI + "delete.department";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("externalId", department.getDepartmentId())
                .queryParam("companyCode", department.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        logger.debug("result URI = " + uriBuilder);

        ResultActions resultActions = mockMvc.perform(
                delete(uriBuilder)
                        .header("Accept", MediaType.APPLICATION_XML)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
        );
        Result result = (Result) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("result = " + result);

        return result;
    }

    private Result<DepartmentRecord> getDepartment(DepartmentRecord department) throws Exception {
        logger.debug("department input = " + department);

        String uri = BASE_URI + "get.department";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("id", department.getDepartmentId())
                .queryParam("companyCode", department.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        ResultActions result = mockMvc.perform(
                get(uriBuilder)
                        .header("Accept", MediaType.APPLICATION_XML)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
        );
        Result<DepartmentRecord> departmentRecord = (Result<DepartmentRecord>) fromXml(result.andReturn().getResponse().getContentAsString());

        logger.debug("DepartmentRecord = " + departmentRecord);

        return departmentRecord;
    }


    private String toXml(Object obj) throws Exception {
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    private Object fromXml(String xml) throws Exception {
        return unmarshaller.unmarshal(new StringReader(xml));
    }

    private void createAndPersistPerson() {
        Person p = new Person();
        String personFirstName = "ws_api";

        p.setCompany(new Company(1L));
        p.setCompanyId(1L);
        p.setFirstName(personFirstName);
        p.setLastName("API");
        p.setDisplayName(personFirstName);
        p.setCreated(new Date());
        p.setCreator("");
        p.setGender(En_Gender.MALE);
        logger.debug("person = " + p);

        personDAO.persist(p);

        person = personDAO
                .getAll()
                .stream()
                .filter(currPerson -> currPerson.getFirstName() != null && currPerson.getFirstName().equals(personFirstName))
                .findFirst().get();
    }

    private void createAndPersistUserRoles() {
        UserRole role = new UserRole();
        role.setCode(WS_API_TEST_ROLE_CODE);
        role.setInfo(WS_API_TEST_ROLE_CODE);
        role.setScope(En_Scope.SYSTEM);
        logger.debug("userRole = " + role);

        userRoleDAO.persist(role);

        userRole = userRoleDAO.getByRoleCodeLike(WS_API_TEST_ROLE_CODE);
    }

    private void createAndPersistUserLogin() throws Exception {
        UserLogin userLogin = userLoginDAO.createNewUserLogin(person);
        userLogin.setUlogin(person.getFirstName());
        userLogin.setUpass(DigestUtils.md5DigestAsHex(QWERTY_PASSWORD.getBytes()));
        userLogin.setPersonId(person.getId());
        userLogin.setAuthType( En_AuthType.LOCAL );
        userLogin.setAdminStateId(2);
        userLogin.setRoles(Collections.singleton(userRole));
        logger.debug("userLogin = " + userLogin);

        userLoginDAO.persist(userLogin);
    }

    private void removeUserLogin() {
        userLoginDAO.removeByPersonId(person.getId());
    }

    private void removeUserRoles() {
        userRoleDAO.removeByRoleCodeLike(WS_API_TEST_ROLE_CODE);
    }

    private void removePerson() {
        personDAO.remove(person);
    }

    private Result<Long> updateWorkerPosition(WorkerRecord newWorkerPosition) throws Exception {
        logger.debug("worker position data = " + newWorkerPosition);

        String uri = BASE_URI + "update.worker.position";

        String workerPositionXml = toXml(newWorkerPosition);

        ResultActions resultActions = mockMvc.perform(
                put(uri)
                        .header("Accept", MediaType.APPLICATION_XML)
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
                        .content(workerPositionXml)
        );

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        return (Result<Long>) fromXml(response.getContentAsString());
    }

    private WorkerEntry createWorker(Long companyId, Long companyDepartmentId, WorkerPosition workerPosition) {
        WorkerEntry worker = new WorkerEntry();
        worker.setPersonId(person.getId());
        worker.setCreated(new Date());
        worker.setCompanyId(companyId);
        worker.setDepartmentId(companyDepartmentId);
        worker.setPositionId(workerPosition.getId());
        worker.setPositionName(workerPosition.getName());
        worker.setExternalId("00000001");
        logger.debug("newWorker = " + worker);
        return worker;
    }

    private WorkerPosition createInitialWorkerPosition(Long companyId) {
        WorkerPosition workerPosition = new WorkerPosition();
        workerPosition.setName("Initial worker position");
        workerPosition.setCompanyId(companyId);
        return workerPosition;
    }

    private WorkerRecord createNewWorkerPosition(String externalId, Long companyDepartmentId) {
        WorkerRecord position = new WorkerRecord();
        position.setWorkerId(externalId);
        position.setCompanyCode("protei");
        position.setNewPositionName("New worker position");
        position.setNewPositionDepartmentId(companyDepartmentId);
        position.setNewPositionTransferDate(new Date());
        logger.debug("worker = " + position);
        return position;
    }

    private CompanyDepartment createCompanyDepartment(Long companyId, String name) {
        CompanyDepartment companyDepartment = new CompanyDepartment();
        companyDepartment.setCreated(new Date());
        companyDepartment.setName(name);
        companyDepartment.setCompanyId(companyId);
        logger.debug("companyDepartment = " + companyDepartment.toString());
        return companyDepartment;
    }
}