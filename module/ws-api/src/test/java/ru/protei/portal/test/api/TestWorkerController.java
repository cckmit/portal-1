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
import ru.protei.portal.core.model.struct.Photo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {APIConfigurationContext.class})
public class TestWorkerController {
    @Autowired
    WebApplicationContext webApplicationContext;
    private static Logger logger = LoggerFactory.getLogger(TestRestService.class);
    private MockMvc mockMvc;
    private static String BASE_URI;
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;

    @BeforeClass
    public static void initClass() throws Exception {
        initJAXB();
        createBaseUri();
    }


    @Before
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        try {
            deleteWorker(createWorkerRecord());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddWorker() throws Exception {
        WorkerRecord worker = createWorkerRecord();
        ServiceResult sr;

        sr = addWorker(new WorkerRecord());
        Assert.assertNotNull("Result add.worker is null!", sr);
        Assert.assertEquals("add.worker: empty add was added! ", false, sr.isSuccess());

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
    }


    @Test
    public void testUpdateWorker() throws Exception {
        WorkerRecord worker = createWorkerRecord();
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
        logger.debug("The worker is updated. id = " + sr.getId());

        worker.setFireDate("2019-05-05");
        worker.setDeleted(true);
        sr = updateWorker(worker);
        Assert.assertNotNull("Result update.worker is null!", sr);
        Assert.assertEquals("update.worker is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("update.worker must return not null identifer!", (sr.getId() != null && sr.getId() > 0));
        logger.debug("The worker is updated. id = " + sr.getId());

        sr = updateWorker(worker);
        Assert.assertNotNull("Result update.worker is null!", sr);
        Assert.assertEquals("update.worker fired worker was updated!", false, sr.isSuccess());
    }


    @Test
    public void testDeleteWorker() throws Exception {
        WorkerRecord worker = createWorkerRecord();
        ServiceResult sr;

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
        logger.debug("The worker is deleted. id = " + sr.getId());

        sr = deleteWorker(worker);
        Assert.assertNotNull("Result delete.worker is null!", sr);
        Assert.assertEquals("delete.worker: the deleted worker was deleted! ", false, sr.isSuccess());

    }

    @Test
    public void testGetPerson() throws Exception {
        String uri = BASE_URI + "get.person";

        WorkerRecord worker = createWorkerRecord();
        ServiceResult sr = addWorker(worker);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri).queryParam("id", sr.getId());
        String uriBuilder = builder.build().toUriString();

        WorkerRecord wr = getWorkerByUri(uriBuilder);

        Assert.assertNotNull("Result of get.person is null!", wr);
        Assert.assertEquals("get.person: added and got person are different", sr.getId(), wr.getId());

        deleteWorker(worker);

        logger.debug("The person is received.");
        logger.debug("id = " + wr.getId());
        logger.debug("firstName = " + wr.getFirstName());
        logger.debug("lastName = " + wr.getLastName());
        logger.debug("secondName = " + wr.getSecondName());
        logger.debug("sex = " + wr.getSex());
        logger.debug("birthday = " + wr.getBirthday());
        logger.debug("phoneWork = " + wr.getPhoneWork());
        logger.debug("phoneHome = " + wr.getPhoneHome());
        logger.debug("phoneMobile = " + wr.getPhoneMobile());
        logger.debug("email = " + wr.getEmail());
        logger.debug("emailOwn = " + wr.getEmailOwn());
        logger.debug("fax = " + wr.getFax());
        logger.debug("address = " + wr.getAddress());
        logger.debug("addressHome = " + wr.getAddressHome());
        logger.debug("passportInfo = " + wr.getPassportInfo());
        logger.debug("info = " + wr.getInfo());
        logger.debug("ipAddress = " + wr.getIpAddress());
        logger.debug("isDeleted = " + wr.isDeleted());
    }

    @Test
    public void testGetWorker() throws Exception {
        String uri = BASE_URI + "get.worker";
        WorkerRecord worker = createWorkerRecord();
        addWorker(worker);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("id", worker.getWorkerId())
                .queryParam("companyCode", worker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        WorkerRecord wr = getWorkerByUri(uriBuilder);

        Assert.assertNotNull("Result of get.worker is null!", wr);
        Assert.assertEquals("get.worker: added and got worker are different", worker.getWorkerId(), wr.getWorkerId());

        deleteWorker(worker);

        logger.debug("The worker is received.");
        logger.debug("id = " + wr.getId());
        logger.debug("firstName = " + wr.getFirstName());
        logger.debug("lastName = " + wr.getLastName());
        logger.debug("secondName = " + wr.getSecondName());
        logger.debug("sex = " + wr.getSex());
        logger.debug("birthday = " + wr.getBirthday());
        logger.debug("phoneWork = " + wr.getPhoneWork());
        logger.debug("phoneHome = " + wr.getPhoneHome());
        logger.debug("phoneMobile = " + wr.getPhoneMobile());
        logger.debug("email = " + wr.getEmail());
        logger.debug("emailOwn = " + wr.getEmailOwn());
        logger.debug("fax = " + wr.getFax());
        logger.debug("address = " + wr.getAddress());
        logger.debug("addressHome = " + wr.getAddressHome());
        logger.debug("passportInfo = " + wr.getPassportInfo());
        logger.debug("info = " + wr.getInfo());
        logger.debug("ipAddress = " + wr.getIpAddress());
        logger.debug("isDeleted = " + wr.isDeleted());
        logger.debug("workerId = " + wr.getWorkerId());
        logger.debug("departmentId = " + wr.getDepartmentId());
        logger.debug("positionName = " + wr.getPositionName());
        logger.debug("hireDate = " + wr.getHireDate());
        logger.debug("hireOrderNo = " + wr.getHireOrderNo());
        logger.debug("active = " + wr.getActive());
    }

    @Test
    public void testGetDepartment() throws Exception {
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);

        DepartmentRecord dr = getDepartment(department);

        Assert.assertNotNull("Result of get.department is null!", dr);
        Assert.assertEquals("get.department: added and got worker are different", department.getDepartmentId(), dr.getDepartmentId());

        logger.debug("The department is received.");
        logger.debug("departmentId = " + dr.getDepartmentId());
        logger.debug("departmentName = " + dr.getDepartmentName());
        logger.debug("parentId = " + dr.getParentId());
        logger.debug("headId = " + dr.getHeadId());

        deleteDepartment(department);
    }

    @Test
    public void testUpdateDepartment() throws Exception {
        DepartmentRecord department = createDepartmentRecord();

        ServiceResult sr = createOrUpdateDepartment(department);

        Assert.assertNotNull("Result update.department is null!", sr);
        Assert.assertEquals("update.department is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("update.department must return not null identifer!", (sr.getId() != null && sr.getId() > 0));
        logger.debug("The department is updated. id = " + sr.getId());

        deleteDepartment(department);
    }

    @Test
    public void testDeleteDepartment() throws Exception {
        DepartmentRecord department = createDepartmentRecord();
        createOrUpdateDepartment(department);

        ServiceResult sr = deleteDepartment(department);

        Assert.assertNotNull("Result delete.department is null!", sr);
        Assert.assertEquals("delete.department is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("delete.department must return not null identifer!", (sr.getId() != null && sr.getId() > 0));
        logger.debug("The department is deleted. id = " + sr.getId());
    }


    @Test
    public void testUpdatePosition() throws Exception {
        String uri = BASE_URI + "update.position";
        WorkerRecord worker = createWorkerRecord();
        String newPosition = createPositionName();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("oldName", worker.getPositionName())
                .queryParam("newName", newPosition)
                .queryParam("companyCode", worker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        ResultActions result = mockMvc.perform(
                put(uriBuilder)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );
        ServiceResult sr = (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());

        Assert.assertNotNull("Result update.position is null!", sr);
        Assert.assertEquals("update.position is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("update.position must return not null identifer!", (sr.getId() != null && sr.getId() > 0));
        logger.debug("The position is updated. id = " + sr.getId());

        deleteWorker(worker);
    }

    @Test
    public void testDeletePosition() throws Exception {

        String uri = BASE_URI + "delete.position";
        WorkerRecord worker = createWorkerRecord();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("name", worker.getPositionName())
                .queryParam("companyCode", worker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        ResultActions result = mockMvc.perform(
                delete(uriBuilder)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );
        ServiceResult sr = (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());

        Assert.assertNotNull("Result delete.position is null!", sr);
        Assert.assertEquals("delete.position is not success! " + sr.getErrInfo(), true, sr.isSuccess());
        Assert.assertTrue("delete.position must return not null identifer!", (sr.getId() != null && sr.getId() > 0));
        logger.debug("The position is deleted. id = " + sr.getId());

        deleteWorker(worker);
    }

 /*   @Test
    public void testPosition() {

        logger.debug ("" + origWorker.getPositionName());
    }*/

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
        InputStream is = null;
        try {
            is = TestSoapService.class.getResourceAsStream("/service.properties");
            Properties props = new Properties();
            props.load(is);
            worker.setCompanyCode(props.getProperty("companyCode"));
            worker.setId(props.getProperty("personId") != null && !props.getProperty("personId").equals("") ? new Long(props.getProperty("personId")) : null);
            worker.setFirstName(props.getProperty("firstName"));
            worker.setLastName(props.getProperty("lastName"));
            worker.setSecondName(props.getProperty("secondName"));
            worker.setSex(new Integer(props.getProperty("sex")));
            worker.setBirthday(props.getProperty("birthday"));
            worker.setPhoneWork(props.getProperty("phoneWork"));
            worker.setPhoneHome(props.getProperty("phoneHome"));
            worker.setPhoneMobile(props.getProperty("phoneMobile"));
            worker.setEmail(props.getProperty("email"));
            worker.setEmailOwn(props.getProperty("emailOwn"));
            worker.setFax(props.getProperty("fax"));
            worker.setAddress(props.getProperty("address"));
            worker.setAddressHome(props.getProperty("addressHome"));
            worker.setPassportInfo(props.getProperty("passportInfo"));
            worker.setInfo(props.getProperty("info"));
            worker.setIpAddress(props.getProperty("ipAddress"));
            worker.setDeleted(new Boolean(props.getProperty("isDeleted")));
            worker.setWorkerId(props.getProperty("workerId"));
            worker.setDepartmentId(props.getProperty("depId"));
            worker.setHireDate(props.getProperty("hireDate"));
            worker.setHireOrderNo(props.getProperty("hireOrderNo"));
            worker.setActive(new Integer(props.getProperty("active")));
            worker.setPositionName(props.getProperty("positionName"));
        } catch (Exception e) {
            logger.error("Can not read config!", e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return worker;
    }

    private DepartmentRecord createDepartmentRecord() {
        DepartmentRecord department = new DepartmentRecord();
        InputStream is = null;
        try {
            is = TestSoapService.class.getResourceAsStream("/service.properties");
            Properties props = new Properties();
            props.load(is);
            department.setCompanyCode(props.getProperty("companyCode"));
            department.setDepartmentId(props.getProperty("departmentId"));
            department.setDepartmentName(props.getProperty("departmentName"));
            department.setParentId(props.getProperty("parentId") != null && !props.getProperty("parentId").equals("") ? props.getProperty("parentId") : null);
            department.setHeadId(props.getProperty("headId") != null && !props.getProperty("headId").equals("") ? props.getProperty("headId") : null);
        } catch (Exception e) {
            logger.error("Can not read config!", e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return department;
    }

    private String createPositionName() {
        String positionName = null;
        InputStream is = null;
        try {
            is = TestSoapService.class.getResourceAsStream("/service.properties");
            Properties props = new Properties();
            props.load(is);
            positionName = props.getProperty("newPositionName");
        } catch (Exception e) {
            logger.error("Can not read config!", e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return positionName;
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

    private static void createBaseUri() throws Exception {
        InputStream is = null;
        try {
            is = TestSoapService.class.getResourceAsStream("/service.properties");
            Properties props = new Properties();
            props.load(is);
            BASE_URI = props.getProperty("service_publish_address");
        } catch (Exception e) {
            logger.error("Can not read config!", e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }


    private ServiceResult addWorker(WorkerRecord worker) throws Exception {
        String uri = BASE_URI + "add.worker";

        String workerXml = toXml(worker);

        ResultActions result = mockMvc.perform(
                post(uri)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(workerXml)
        );
        return (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());
    }


    private ServiceResult updateWorker(WorkerRecord worker) throws Exception {
        String uri = BASE_URI + "update.worker";

        String workerXml = toXml(worker);

        ResultActions result = mockMvc.perform(
                put(uri)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(workerXml)
        );
        return (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());
    }


    private ServiceResult deleteWorker(WorkerRecord worker) throws Exception {
        String uri = BASE_URI + "delete.worker";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("externalId", worker.getWorkerId())
                .queryParam("companyCode", worker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        ResultActions result = mockMvc.perform(
                delete(uriBuilder)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );
        return (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());
    }

    private WorkerRecord getWorkerByUri(String uri) throws Exception {
        ResultActions result = mockMvc.perform(
                get(uri)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );

        return (WorkerRecord) fromXml(result.andReturn().getResponse().getContentAsString());
    }

    private ServiceResult createOrUpdateDepartment(DepartmentRecord department) throws Exception {
        String uri = BASE_URI + "update.department";

        String departmentXml = toXml(department);

        ResultActions result = mockMvc.perform(
                put(uri)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(departmentXml)
        );
        return (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());
    }

    private ServiceResult deleteDepartment(DepartmentRecord department) throws Exception {
        String uri = BASE_URI + "delete.department";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("externalId", department.getDepartmentId())
                .queryParam("companyCode", department.getCompanyCode());
        String uriBuilder = builder.build().toUriString();

        ResultActions result = mockMvc.perform(
                delete(uriBuilder)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
        );
        return (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());
    }

    private DepartmentRecord getDepartment(DepartmentRecord department) throws Exception {
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
        return (DepartmentRecord) fromXml(result.andReturn().getResponse().getContentAsString());
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