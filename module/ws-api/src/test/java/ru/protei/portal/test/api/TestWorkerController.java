package ru.protei.portal.test.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.config.APIConfigurationContext;
import ru.protei.portal.api.model.*;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.core.model.struct.Photo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {APIConfigurationContext.class, DatabaseConfiguration.class})
public class TestWorkerController {

    @Autowired
    WebApplicationContext webApplicationContext;
    private static Logger logger = LoggerFactory.getLogger(TestWorkerController.class);
    private static MockMvc mockMvc;
    private static String BASE_URI;
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;

    @BeforeClass
    public static void initClass() throws Exception {
        initJAXB();
        BASE_URI = "http://localhost:8090/api/worker/";
    }

    @Before
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testAddWorker() throws Exception {
        logger.debug("START =======================================================================");

        WorkerRecord worker = createWorkerRecord();
        ServiceResult sr;
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);

        sr = addWorker(new WorkerRecord());
        Assert.assertNotNull("Result add.worker is null!", sr);
        Assert.assertEquals("add.worker: empty worker was added! ", false, sr.isSuccess());

        worker.setFireDate("2019-05-05");
        sr = addWorker(worker);
        Assert.assertNotNull("Result add.worker is null!", sr);
        Assert.assertEquals("add.worker: fired worker was added! ", false, sr.isSuccess());

        worker.setFireDate(null);
        worker.setFired(false);
        sr = addWorker(worker);
        Assert.assertNotNull("Result add.worker is null!", sr);
        Assert.assertEquals("add.worker is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("add.worker must return not null identifer!", (sr.getId() != null && sr.getId() > 0));

        sr = addWorker(worker);
        Assert.assertNotNull("Result add.worker is null!", sr);
        Assert.assertEquals("add.worker: already exist worker was added! " + sr.getErrInfo(), false, sr.isSuccess());

        deleteWorker(worker);
        deleteDepartment(department);

        logger.debug("END =========================================================================");
    }


    @Test
    public void testUpdateWorker() throws Exception {
        logger.debug("START =======================================================================");

        WorkerRecord worker = createWorkerRecord();
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        ServiceResult successServiceResult = addWorker(worker);
        ServiceResult sr;


        sr = updateWorker(worker);
        Assert.assertNotNull("Result update.worker is null!", sr);
        Assert.assertEquals("update.worker worker with nonexistent personId was updated!", false, sr.isSuccess());

        worker.setId(successServiceResult.getId());
        sr = updateWorker(worker);
        Assert.assertNotNull("Result update.worker is null!", sr);
        Assert.assertEquals("update.worker is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("update.worker must return not null identifer!", (sr.getId() != null && sr.getId() > 0));

        worker.setFireDate("2019-05-05");
        worker.setDeleted(true);
        sr = updateWorker(worker);
        Assert.assertNotNull("Result update.worker is null!", sr);
        Assert.assertEquals("update.worker is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("update.worker must return not null identifer!", (sr.getId() != null && sr.getId() > 0));

        sr = updateWorker(worker);
        Assert.assertNotNull("Result update.worker is null!", sr);
        Assert.assertEquals("update.worker fired worker was updated!", false, sr.isSuccess());

        deleteDepartment(department);

        logger.debug("END =========================================================================");
    }


    @Test
    public void testDeleteWorker() throws Exception {
        logger.debug("START =======================================================================");

        WorkerRecord worker = createWorkerRecord();
        ServiceResult sr;
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);

        WorkerRecord emptyWorker = new WorkerRecord();
        emptyWorker.setWorkerId(worker.getWorkerId());
        emptyWorker.setCompanyCode(worker.getCompanyCode());
        sr = deleteWorker(emptyWorker);
        Assert.assertNotNull("Result delete.worker is null!", sr);
        Assert.assertEquals("delete.worker: empty worker was deleted! ", false, sr.isSuccess());

        addWorker(worker);
        sr = deleteWorker(worker);
        Assert.assertNotNull("Result delete.worker is null!", sr);
        Assert.assertEquals("delete.worker is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("delete.worker must return not null identifer!", (sr.getId() != null && sr.getId() > 0));

        sr = deleteWorker(worker);
        Assert.assertNotNull("Result delete.worker is null!", sr);
        Assert.assertEquals("delete.worker: the deleted worker was deleted! ", false, sr.isSuccess());

        deleteDepartment(department);

        logger.debug("END =========================================================================");
    }

    @Test
    public void testGetPerson() throws Exception {
        logger.debug("START =======================================================================");

        String uri = BASE_URI + "get.person";

        WorkerRecord worker = createWorkerRecord();
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        ServiceResult sr = addWorker(worker);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri).queryParam("id", sr.getId());
        String uriBuilder = builder.build().toUriString();

        WorkerRecord wr = getWorkerByUri(uriBuilder);

        Assert.assertNotNull("Result of get.person is null!", wr);
        Assert.assertEquals("get.person: added and got person are different", sr.getId(), wr.getId());

        deleteWorker(worker);
        deleteDepartment(department);

        logger.debug("END =========================================================================");
    }

    @Test
    public void testGetWorker() throws Exception {
        logger.debug("START =======================================================================");

        String uri = BASE_URI + "get.worker";
        WorkerRecord worker = createWorkerRecord();
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);
        addWorker(worker);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("id", worker.getWorkerId())
                .queryParam("companyCode", worker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        WorkerRecord wr = getWorkerByUri(uriBuilder);

        Assert.assertNotNull("Result of get.worker is null!", wr);
        Assert.assertEquals("get.worker: added and got worker are different", worker.getWorkerId(), wr.getWorkerId());

        deleteWorker(worker);
        deleteDepartment(department);

        logger.debug("END =========================================================================");
    }

    @Test
    public void testGetDepartment() throws Exception {
        logger.debug("START =======================================================================");

        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);

        DepartmentRecord dr = getDepartment(department);

        Assert.assertNotNull("Result of get.department is null!", dr);
        Assert.assertEquals("get.department: added and got worker are different", department.getDepartmentId(), dr.getDepartmentId());

        deleteDepartment(department);

        logger.debug("END =========================================================================");
    }

    @Test
    public void testUpdateDepartment() throws Exception {
        logger.debug("START =======================================================================");

        DepartmentRecord department = createDepartmentRecord();

        ServiceResult sr = createOrUpdateDepartment(department);

        Assert.assertNotNull("Result update.department is null!", sr);
        Assert.assertEquals("update.department is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("update.department must return not null identifer!", (sr.getId() != null && sr.getId() > 0));

        deleteDepartment(department);

        logger.debug("END =========================================================================");
    }

    @Test
    public void testDeleteDepartment() throws Exception {
        logger.debug("START =======================================================================");

        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);

        ServiceResult sr = deleteDepartment(department);

        Assert.assertNotNull("Result delete.department is null!", sr);
        Assert.assertEquals("delete.department is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("delete.department must return not null identifer!", (sr.getId() != null && sr.getId() > 0));

        logger.debug("END =========================================================================");
    }


    @Test
    public void testUpdatePosition() throws Exception {
        logger.debug("START =======================================================================");

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

        ResultActions result = mockMvc.perform(
                put(uriBuilder)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );
        ServiceResult sr = (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());

        logger.debug("ServiceResult = " + sr);

        Assert.assertNotNull("Result update.position is null!", sr);
        Assert.assertEquals("update.position is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("update.position must return not null identifer!", (sr.getId() != null && sr.getId() > 0));

        deleteWorker(worker);
        deleteDepartment(department);

        logger.debug("END =========================================================================");
    }

    @Test
    public void testDeletePosition() throws Exception {
        logger.debug("START =======================================================================");

        String uri = BASE_URI + "delete.position";
        WorkerRecord worker = createWorkerRecord();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("name", worker.getPositionName())
                .queryParam("companyCode", worker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        logger.debug("result URI = " + uriBuilder);

        ResultActions result = mockMvc.perform(
                delete(uriBuilder)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );
        ServiceResult sr = (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());

        logger.debug("ServiceResult = " + sr);

        Assert.assertNotNull("Result delete.position is null!", sr);
        Assert.assertEquals("delete.position is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("delete.position must return not null identifer!", (sr.getId() != null && sr.getId() > 0));
        logger.debug("The position is deleted. id = " + sr.getId());

        deleteWorker(worker);

        logger.debug("END =========================================================================");
    }


  /*  @Test
    public void testUpdatePhoto() {

        Long id = new Long (140);
        byte[] buf = read (id);
        logger.debug ("personId = " + id);
        logger.debug ("photo = " + buf);
        logger.debug("photo's length = " + (buf != null ? buf.length : null));

        String URI = BASE_URI + "update.photo";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));

        Photo photo = new Photo();
        photo.setId(origWorker.getId());
        photo.setContent(Base64.getEncoder().encodeToString(buf));
        HttpEntity<Photo> entity = new HttpEntity<>(photo, headers);

        ResponseEntity<ServiceResult> response = restTemplate.exchange(URI, HttpMethod.PUT, entity, ServiceResult.class);
        ServiceResult sr = response.getBody();

        Assert.assertNotNull ("Result updatePhoto() is null!", sr);
        Assert.assertEquals ("updatePhoto() is not success! " + sr.getErrInfo (), true, sr.isSuccess ());
        Assert.assertTrue ("updatePhoto() must return not null identifer!", (sr.getId () != null && sr.getId () > 0));
        logger.debug ("The photo of worker is updated. id = " + sr.getId ());
    }*/
/*
    @Test
    public void testGetPhotos() {
        IdList list = new IdList ();
        list.getIds().add (new Long (148));
        list.getIds().add (new Long (149));

        String URI = BASE_URI + "get.photos";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<IdList> entity = new HttpEntity<>(list, headers);

        ResponseEntity<PhotoList> response = restTemplate.exchange(URI, HttpMethod.POST, entity, PhotoList.class);
        PhotoList pl = response.getBody();

        Assert.assertNotNull ("Result getPhotos() is null!", pl);
        for (Photo p : pl.getPhotos()) {
            logger.debug ("Photo for id = " + p.getId () + " exist. Length of photo = " + p.getContent ().length());
            logger.debug("Photo's content in Base64 = " + p.getContent());
            String newFileName = WSConfig.getInstance ().getDirPhotos () + "new/" + p.getId() + ".jpg";
            Base64OutputStream out = null;
            try {
                out = new Base64OutputStream(new FileOutputStream(newFileName),false);
                out.write (p.getContent().getBytes());
                //out.write (p.getContent());
            } catch (Exception e){
                logger.error(e.getMessage(), e);
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (Exception e) {}
            }
        }
    }

*/

    private WorkerRecord createWorkerRecord() {
        WorkerRecord worker = new WorkerRecord();

        worker.setCompanyCode("protei");
        worker.setId(100000L + new Random().nextInt(100000));
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
                ServiceResult.class, ServiceResultList.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        unmarshaller = context.createUnmarshaller();
    }


    private ServiceResult addWorker(WorkerRecord worker) throws Exception {
        logger.debug("worker input = " + worker);

        String uri = BASE_URI + "add.worker";

        String workerXml = toXml(worker);

        ResultActions result = mockMvc.perform(
                post(uri)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(workerXml)
        );
        ServiceResult serviceResult = (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());

        logger.debug("ServiceResult = " + serviceResult);

        return serviceResult;
    }


    private ServiceResult updateWorker(WorkerRecord worker) throws Exception {
        logger.debug("worker input = " + worker);

        String uri = BASE_URI + "update.worker";

        String workerXml = toXml(worker);

        ResultActions result = mockMvc.perform(
                put(uri)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(workerXml)
        );
        ServiceResult serviceResult = (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());

        logger.debug("ServiceResult = " + serviceResult);

        return serviceResult;
    }


    private ServiceResult deleteWorker(WorkerRecord worker) throws Exception {
        String uri = BASE_URI + "delete.worker";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("externalId", worker.getWorkerId())
                .queryParam("companyCode", worker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        logger.debug("result URI = " + uriBuilder);

        ResultActions result = mockMvc.perform(
                delete(uriBuilder)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );

        ServiceResult serviceResult = (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());

        logger.debug("ServiceResult = " + serviceResult);

        return serviceResult;
    }

    private WorkerRecord getWorkerByUri(String uri) throws Exception {
        logger.debug("result URI = " + uri);

        ResultActions result = mockMvc.perform(
                get(uri)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );

        WorkerRecord workerRecord = (WorkerRecord) fromXml(result.andReturn().getResponse().getContentAsString());

        logger.debug("WorkerRecord = " + workerRecord);

        return workerRecord;
    }

    private ServiceResult createOrUpdateDepartment(DepartmentRecord department) throws Exception {
        logger.debug("department input = " + department);

        String uri = BASE_URI + "update.department";

        String departmentXml = toXml(department);

        ResultActions result = mockMvc.perform(
                put(uri)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(departmentXml)
        );

        ServiceResult serviceResult = (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());

        logger.debug("ServiceResult = " + serviceResult);

        return serviceResult;
    }

    private ServiceResult deleteDepartment(DepartmentRecord department) throws Exception {
        String uri = BASE_URI + "delete.department";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("externalId", department.getDepartmentId())
                .queryParam("companyCode", department.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        logger.debug("result URI = " + uriBuilder);

        ResultActions result = mockMvc.perform(
                delete(uriBuilder)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );
        ServiceResult serviceResult = (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());

        logger.debug("ServiceResult = " + serviceResult);

        return serviceResult;
    }

    private DepartmentRecord getDepartment(DepartmentRecord department) throws Exception {
        logger.debug("department input = " + department);

        String uri = BASE_URI + "get.department";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("id", department.getDepartmentId())
                .queryParam("companyCode", department.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        ResultActions result = mockMvc.perform(
                get(uriBuilder)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );
        DepartmentRecord departmentRecord = (DepartmentRecord) fromXml(result.andReturn().getResponse().getContentAsString());

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
}