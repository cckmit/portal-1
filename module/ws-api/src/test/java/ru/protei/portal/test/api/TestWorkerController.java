package ru.protei.portal.test.api;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.struct.Photo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
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
    private String WS_API_TEST_ROLE_CODE = "ws_api_test_role" + System.currentTimeMillis();
    private String QWERTY_PASSWORD = "qwerty_test_API" + new Date().getTime();
    private UserRole userRole;
    private Person person;

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
        createAndPersistPerson();
        createAndPersistUserRoles();
        createAndPersistUserLogin();
    }

    @After
    public void removePersonToAuth() {
        removeUserLogin();
        removeUserRoles();
        removePerson();
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
        Result<Long> successResult = addWorker(worker);
        Result result;

        worker.setId(successResult.getData());

        worker.setFired(true);
        result = updateWorker(worker);
        Assert.assertEquals("update.fire.date is not success! " + result.getMessage(), true, result.isOk());

        String uri = BASE_URI + "get.person";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri).queryParam("id", successResult.getData());
        String uriBuilder = builder.build().toUriString();

        WorkerRecord resultWorker = getWorkerByUri(uriBuilder).getData();

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
    }


    @Test
    public void testUpdateFireDates() throws Exception {
        WorkerRecord worker = createWorkerRecord();
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        Result<Long> successResult = addWorker(worker);
        Result result;

        worker.setId(successResult.getData());

        worker.setFired(true);
        result = updateWorker(worker);
        Assert.assertEquals("update.fire.date is not success! " + result.getMessage(), true, result.isOk());

        String uri = BASE_URI + "get.person";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri).queryParam("id", successResult.getData());
        String uriBuilder = builder.build().toUriString();

        WorkerRecord resultWorker = getWorkerByUri(uriBuilder).getData();

        Assert.assertEquals("update.fire.date: worker is not fired!", worker.isFired(), resultWorker.isFired());

        resultWorker.setFireDate("2019-05-05");
        worker.setFireDate("2019-06-06");

        WorkerRecordList list = new WorkerRecordList();
        list.append(worker);
        list.append(resultWorker);

        ResultList resultList = updateFireDates(list);

        List<Result> results = resultList.getResults();

        for (Result result1 : results) {
            Assert.assertEquals("update.fire.date is not success! " + result.getMessage(), true, result.isOk());
        }

        updateFireDate(worker);
        resultWorker = getWorkerByUri(uriBuilder).getData();

        Assert.assertEquals("update.fire.date: fire date are different!", worker.getFireDate(), resultWorker.getFireDate());
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
    @Ignore
    public void testUpdatePhoto() throws Exception {
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        WorkerRecord worker = createWorkerRecord();
        Result<Long> result = addWorker(worker);

        Long id = result.getData();
        byte[] buf = read(id);

        String uri = BASE_URI + "update.photo";

        Photo photo = new Photo();
        photo.setId(result.getData());
        photo.setContent(Base64.getEncoder().encodeToString(buf));
        String photoXml = toXml(photo);

        ResultActions resultActions = mockMvc.perform(
                put(uri)
                        .header("Accept", "application/xml")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
                        .content(photoXml)
        );
        result = (Result<Long>) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        Assert.assertEquals("updatePhoto() is not success! " + result.getMessage(), true, result.isOk());
    }

    @Test
    @Ignore
    public void testGetPhotos() throws Exception {
        IdList list = new IdList();
        list.getIds().add(new Long(148));
        list.getIds().add(new Long(149));

        String uri = BASE_URI + "get.photos";
        ResultActions result = mockMvc.perform(
                get(uri)
                        .header("Accept", "application/xml")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
        );


        PhotoList pl = (PhotoList) fromXml(result.andReturn().getResponse().getContentAsString());

        Assert.assertNotNull("Result getPhotos() is null!", pl);
        for (Photo p : pl.getPhotos()) {
            logger.debug("Photo for id = " + p.getId() + " exist. Length of photo = " + p.getContent().length());
            logger.debug("Photo's content in Base64 = " + p.getContent());
            String newFileName = WSConfig.getInstance().getDirPhotos() + "new/" + p.getId() + ".jpg";
            Base64OutputStream out = null;
            try {
                out = new Base64OutputStream(new FileOutputStream(newFileName), false);
                out.write(p.getContent().getBytes());
                //out.write (p.getContent());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (Exception e) {
                }
            }
        }
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
                Result.class);
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
                        .header("Accept", "application/xml")
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
                        .header("Accept", "application/xml")
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
                        .header("Accept", "application/xml")
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

        String uri = BASE_URI + "update.fire.date";

        String listXml = toXml(list);

        ResultActions resultActions = mockMvc.perform(
                put(uri)
                        .header("Accept", "application/xml")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
                        .content(listXml)
        );
        ResultList result = (ResultList) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("result = " + result);

        return result;
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
                        .header("Accept", "application/xml")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
        );

        Result result = (Result) fromXml(resultActions.andReturn().getResponse().getContentAsString());

        logger.debug("result = " + result);

        return result;
    }

    private Result<WorkerRecord> getWorkerByUri(String uri) throws Exception {
        logger.debug("result URI = " + uri);

        ResultActions result = mockMvc.perform(
                get(uri)
                        .header("Accept", "application/xml")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
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
                        .header("Accept", "application/xml")
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
                        .header("Accept", "application/xml")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
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
                        .header("Accept", "application/xml")
                        .header("authorization", "Basic " + Base64.getEncoder().encodeToString((person.getFirstName() + ":" + QWERTY_PASSWORD).getBytes()))
                        .contentType(MediaType.APPLICATION_XML)
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

    private byte[] read(Long id) {

        ByteArrayOutputStream out = null;
        InputStream input = null;

        try {
            String fileName = id + ".jpg";
            logger.debug("fileName = " + fileName);
            File file = new File(getClass().getClassLoader().getResource("source.jpg").getFile());
            if (file.exists()) {
                copy(file.getAbsolutePath(), file.getParent() + "/" + id + ".jpg");
                out = new ByteArrayOutputStream();
                input = new BufferedInputStream(new FileInputStream(file));
                int data = 0;
                while ((data = input.read()) != -1) {
                    out.write(data);
                }
                logger.debug("file exists");
            } else {
                logger.debug("file doesn't exist");
            }
        } catch (Exception e) {
            logger.error("error while update photo", e);
        } finally {
            try {
                input.close();
                out.close();
            } catch (Exception e) {
            }
        }
        return out.toByteArray();
    }

    public static void copy(String resourceFileName, String destinationFileName) throws IOException {
        if (Files.exists(Paths.get(destinationFileName))) Files.delete(Paths.get(destinationFileName));
        Files.copy(Paths.get(resourceFileName), Paths.get(destinationFileName));
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
        logger.debug("person = " + person);

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
        logger.debug("userRole = " + userRole);

        userRoleDAO.persist(role);

        userRole = userRoleDAO.getByRoleCodeLike(WS_API_TEST_ROLE_CODE);
    }

    private void createAndPersistUserLogin() throws Exception {
        UserLogin userLogin = userLoginDAO.createNewUserLogin(person);
        userLogin.setUlogin(person.getFirstName());
        userLogin.setUpass(DigestUtils.md5DigestAsHex(QWERTY_PASSWORD.getBytes()));
        userLogin.setPersonId(person.getId());
        userLogin.setAuthTypeId(1);
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
}