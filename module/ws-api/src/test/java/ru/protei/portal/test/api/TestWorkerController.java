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
    private static DepartmentRecord origDepartment = new DepartmentRecord();
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;

    @BeforeClass
    public static void initClass() throws Exception {
        JAXBContext context = JAXBContext.newInstance(WorkerRecord.class, WorkerRecordList.class,
                DepartmentRecord.class, IdList.class,
                Photo.class, PhotoList.class,
                ServiceResult.class, ServiceResultList.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        unmarshaller = context.createUnmarshaller();
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


    private WorkerRecord createWorkerRecord() {
        WorkerRecord origWorker = new WorkerRecord();
        InputStream is = null;
        try {
            is = TestSoapService.class.getResourceAsStream("/service.properties");
            Properties props = new Properties();
            props.load(is);
            BASE_URI = props.getProperty("service_publish_address");
            origDepartment.setCompanyCode(props.getProperty("companyCode"));
            origDepartment.setDepartmentId(props.getProperty("departmentId"));
            origDepartment.setDepartmentName(props.getProperty("departmentName"));
            origDepartment.setParentId(props.getProperty("parentId") != null && !props.getProperty("parentId").equals("") ? props.getProperty("parentId") : null);
            origDepartment.setHeadId(props.getProperty("headId") != null && !props.getProperty("headId").equals("") ? props.getProperty("headId") : null);
            origWorker.setCompanyCode(props.getProperty("companyCode"));
            origWorker.setId(props.getProperty("personId") != null && !props.getProperty("personId").equals("") ? new Long(props.getProperty("personId")) : null);
            origWorker.setFirstName(props.getProperty("firstName"));
            origWorker.setLastName(props.getProperty("lastName"));
            origWorker.setSecondName(props.getProperty("secondName"));
            origWorker.setSex(new Integer(props.getProperty("sex")));
            origWorker.setBirthday(props.getProperty("birthday"));
            origWorker.setPhoneWork(props.getProperty("phoneWork"));
            origWorker.setPhoneHome(props.getProperty("phoneHome"));
            origWorker.setPhoneMobile(props.getProperty("phoneMobile"));
            origWorker.setEmail(props.getProperty("email"));
            origWorker.setEmailOwn(props.getProperty("emailOwn"));
            origWorker.setFax(props.getProperty("fax"));
            origWorker.setAddress(props.getProperty("address"));
            origWorker.setAddressHome(props.getProperty("addressHome"));
            origWorker.setPassportInfo(props.getProperty("passportInfo"));
            origWorker.setInfo(props.getProperty("info"));
            origWorker.setIpAddress(props.getProperty("ipAddress"));
            origWorker.setDeleted(new Boolean(props.getProperty("isDeleted")));
            origWorker.setWorkerId(props.getProperty("workerId"));
            origWorker.setDepartmentId(props.getProperty("depId"));
            origWorker.setHireDate(props.getProperty("hireDate"));
            origWorker.setHireOrderNo(props.getProperty("hireOrderNo"));
            origWorker.setActive(new Integer(props.getProperty("active")));
            origWorker.setPositionName(props.getProperty("positionName"));
        } catch (Exception e) {
            logger.error("Can not read config!", e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return origWorker;
    }


    private ServiceResult addWorker(WorkerRecord worker) throws Exception {
        String URI = BASE_URI + "add.worker";

        String workerXml = toXml(worker);

        ResultActions result = mockMvc.perform(
                post(URI)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(workerXml)
        );
        return (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());
    }


    private ServiceResult updateWorker(WorkerRecord worker) throws Exception {
        String URI = BASE_URI + "update.worker";

        String workerXml = toXml(worker);

        ResultActions result = mockMvc.perform(
                put(URI)
                        .header("Accept", "application/xml")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(workerXml)
        );
        return (ServiceResult) fromXml(result.andReturn().getResponse().getContentAsString());
    }


    private ServiceResult deleteWorker(WorkerRecord worker) throws Exception {
        String URI = BASE_URI + "delete.worker";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URI)
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


    private String toXml(Object obj) throws Exception {
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    private Object fromXml(String xml) throws Exception {
        return unmarshaller.unmarshal(new StringReader(xml));
    }


}