package ru.protei.portal.test.api;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.config.APIConfigurationContext;
import ru.protei.portal.api.model.*;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.query.CaseApiQuery;
import ru.protei.portal.core.model.struct.Photo;
import ru.protei.winter.core.CoreConfigurationContext;

/*import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;*/

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Base64;
import java.util.Properties;

import static com.sun.org.apache.xerces.internal.util.PropertyState.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {APIConfigurationContext.class})
public class TestWorkerController {
    @Autowired
    WebApplicationContext webApplicationContext;

    private static Logger logger = LoggerFactory.getLogger(TestRestService.class);

    private MockMvc mockMvc;
    private static String BASE_URI;

    private static DepartmentRecord origDepartment = new DepartmentRecord ();
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;
    private static   WorkerRecord origWorker = new WorkerRecord();

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

            BASE_URI = props.getProperty ("service_publish_address");

            origDepartment.setCompanyCode (props.getProperty ("companyCode"));
            origDepartment.setDepartmentId(props.getProperty ("departmentId"));
            origDepartment.setDepartmentName (props.getProperty ("departmentName"));
            origDepartment.setParentId (props.getProperty ("parentId") != null && !props.getProperty ("parentId").equals ("") ? props.getProperty ("parentId") : null );
            origDepartment.setHeadId (props.getProperty ("headId") != null && !props.getProperty ("headId").equals ("") ? props.getProperty ("headId") : null);

            origWorker.setCompanyCode (props.getProperty ("companyCode"));

            //origWorker.setCompanyCode (companyCode);
            origWorker.setId (props.getProperty ("personId") != null && !props.getProperty ("personId").equals ("") ? new Long (props.getProperty ("personId")) : null);
            origWorker.setFirstName (props.getProperty ("firstName"));
            origWorker.setLastName (props.getProperty ("lastName"));
            origWorker.setSecondName (props.getProperty ("secondName"));
            origWorker.setSex (new Integer (props.getProperty ("sex")));
            origWorker.setBirthday (props.getProperty ("birthday"));
            origWorker.setPhoneWork (props.getProperty ("phoneWork"));
            origWorker.setPhoneHome (props.getProperty ("phoneHome"));
            origWorker.setPhoneMobile (props.getProperty ("phoneMobile"));
            origWorker.setEmail (props.getProperty ("email"));
            origWorker.setEmailOwn (props.getProperty ("emailOwn"));
            origWorker.setFax (props.getProperty ("fax"));
            origWorker.setAddress (props.getProperty ("address"));
            origWorker.setAddressHome (props.getProperty ("addressHome"));
            origWorker.setPassportInfo (props.getProperty ("passportInfo"));
            origWorker.setInfo (props.getProperty ("info"));
            origWorker.setIpAddress (props.getProperty ("ipAddress"));
            origWorker.setDeleted (new Boolean (props.getProperty ("isDeleted")));
            origWorker.setWorkerId(props.getProperty ("workerId"));
            origWorker.setDepartmentId(props.getProperty ("depId"));
            origWorker.setHireDate (props.getProperty ("hireDate"));
            origWorker.setHireOrderNo (props.getProperty ("hireOrderNo"));
            origWorker.setActive (new Integer (props.getProperty ("active")));
            origWorker.setPositionName (props.getProperty ("positionName"));
        } catch (Exception e) {
            logger.error ("Can not read config!", e);
        } finally {
            try {
                is.close ();
            } catch (Exception e) {}
        }




    }
    @Before
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testAddWorker() throws Exception {
        String URI = BASE_URI + "add.worker";


ServiceResult sr = test(URI, origWorker, "POST");
        Assert.assertNotNull ("Result add.worker is null!", sr);
        Assert.assertEquals ("add.worker is not success! " + sr.getErrInfo (), true, sr.isSuccess ());
        Assert.assertTrue ("add.worker must return not null identifer!", (sr.getId () != null && sr.getId () > 0));
        origWorker.setId (sr.getId ());

    }

    @Test
    public void testUpdateWorker() throws Exception{
        String URI = BASE_URI + "update.worker";

        //origWorker.setFireDate("");
        //origWorker.setId(7995L);

        ServiceResult sr = test(URI, origWorker, "PUT");

        Assert.assertNotNull ("Result update.worker is null!", sr);
        Assert.assertEquals ("update.worker is not success! " + sr.getErrInfo (), true, sr.isSuccess ());
        Assert.assertTrue ("update.worker must return not null identifer!", (sr.getId () != null && sr.getId () > 0));
        logger.debug ("The worker is updated. id = " + sr.getId ());
    }

    @Test
    public void testDeleteWorker() throws Exception {

        String URI = BASE_URI + "delete.worker";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URI)
                .queryParam("externalId", origWorker.getWorkerId())
                .queryParam("companyCode", origWorker.getCompanyCode());
        String uriBuilder = builder.build().toUriString();


        ServiceResult sr = test(uriBuilder, origWorker, "DELETE");

        Assert.assertNotNull ("Result delete.worker is null!", sr);
        Assert.assertEquals ("delete.worker is not success! " + sr.getErrInfo (), true, sr.isSuccess ());
        Assert.assertTrue ("delete.worker must return not null identifer!", (sr.getId () != null && sr.getId () > 0));
        logger.debug ("The worker is deleted. id = " + sr.getId ());
    }


    private <K, T> K test(String url, T obj, String method) throws Exception{

        StringWriter writer = new StringWriter();




        marshaller.marshal(obj, writer);

        String result = writer.toString();

        ResultActions test;
        switch (method.toUpperCase()) {
           case ("POST") :
               test = mockMvc.perform(
                       post(url)
                               .header("Accept", "application/xml")
                               .contentType(MediaType.APPLICATION_XML)
                               .content(result)
               );
               break;
            case ("DELETE") :
                test = mockMvc.perform(
                        delete(url)
                                .header("Accept", "application/xml")
                                .contentType(MediaType.APPLICATION_XML)
                                .content(result)
                );
                break;
            case ("PUT") :
                test = mockMvc.perform(
                        put(url)
                                .header("Accept", "application/xml")
                                .contentType(MediaType.APPLICATION_XML)
                                .content(result)
                );
                break;
            default: return null;
        }


        String result2 = test.andReturn().getResponse().getContentAsString();

        StringReader reader = new StringReader(result2);




        return (K) unmarshaller.unmarshal(reader);

    }


}