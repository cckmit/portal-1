package ru.protei.portal.test.api;

import junit.framework.Assert;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.config.WSConfig;
import ru.protei.portal.api.model.*;
import ru.protei.portal.api.service.WorkerService;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TestRestService {
    private static Logger logger = Logger.getLogger(TestRestService.class);

    private static String BASE_URI;
    private static String dirPhotos;
    private static DepartmentRecord origDepartment = new DepartmentRecord ();
    private static WorkerRecord origWorker = new WorkerRecord ();

    @BeforeClass
    public static void setUpClass() {

        InputStream is = null;

        try {

            is = TestSoapService.class.getResourceAsStream("/service.properties");
            Properties props = new Properties();
            props.load(is);

            BASE_URI = props.getProperty ("service_publish_address");
            dirPhotos = props.getProperty ("dir_photos");

            origDepartment.setCompanyCode (props.getProperty ("companyCode"));
            origDepartment.setDepartmentId (new Long (props.getProperty ("departmentId")));
            origDepartment.setDepartmentName (props.getProperty ("departmentName"));
            origDepartment.setParentId (props.getProperty ("parentId") != null && !props.getProperty ("parentId").equals ("") ? new Long(props.getProperty ("parentId")) : null );
            origDepartment.setHeadId (props.getProperty ("headId") != null && !props.getProperty ("headId").equals ("") ? new Long (props.getProperty ("headId")) : null);

            origWorker.setCompanyCode (props.getProperty ("companyCode"));
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
            origWorker.setFired (new Boolean (props.getProperty ("isFired")));
            origWorker.setWorkerId (new Long (props.getProperty ("workerId")));
            origWorker.setDepartmentId (new Long (props.getProperty ("depId")));
            origWorker.setPositionId (new Long (props.getProperty ("positionId")));
            origWorker.setHireDate (props.getProperty ("hireDate"));
            origWorker.setFireDate (props.getProperty ("fireDate"));
            origWorker.setHireOrderNo (props.getProperty ("hireOrderNo"));
            origWorker.setFireOrderNo (props.getProperty ("fireOrderNo"));
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

    @Test
    public void testGetWorker() {
        String URI = BASE_URI + "get.worker";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URI).queryParam("id", 148L);
        String uriBuilder = builder.build().encode().toUriString();

        ResponseEntity<WorkerRecord> response = restTemplate.exchange(uriBuilder, HttpMethod.GET, entity, WorkerRecord.class);
        WorkerRecord wr = response.getBody();

        Assert.assertNotNull ("Result of get.worker is null!", wr);
        logger.debug ("The worker is received.");
        logger.debug ("id = " + wr.getId ());
        logger.debug ("firstName = " + wr.getFirstName ());
        logger.debug ("lastName = " + wr.getLastName ());
        logger.debug ("secondName = " + wr.getSecondName ());
        logger.debug ("sex = " + wr.getSex ());
        logger.debug ("birthday = " + wr.getBirthday ());
        logger.debug ("phoneWork = " + wr.getPhoneWork ());
        logger.debug ("phoneHome = " + wr.getPhoneHome ());
        logger.debug ("phoneMobile = " + wr.getPhoneMobile ());
        logger.debug ("email = " + wr.getEmail ());
        logger.debug ("emailOwn = " + wr.getEmailOwn ());
        logger.debug ("fax = " + wr.getFax ());
        logger.debug ("address = " + wr.getAddress ());
        logger.debug ("addressHome = " + wr.getAddressHome ());
        logger.debug ("passportInfo = " + wr.getPassportInfo ());
        logger.debug ("info = " + wr.getInfo ());
        logger.debug ("ipAddress = " + wr.getIpAddress ());
        logger.debug ("isDeleted = " + wr.isDeleted ());
    }

    @Test
    public void testGetDepartment() {
        String URI = BASE_URI + "get.department";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<DepartmentRecord> response = restTemplate.exchange(URI, HttpMethod.GET, entity, DepartmentRecord.class);
        DepartmentRecord dr = response.getBody();

        Assert.assertNotNull ("Result of get.department is null!", dr);
        logger.debug ("The department is received.");
        logger.debug ("departmentId = " + dr.getDepartmentId());
        logger.debug ("departmentName = " + dr.getDepartmentName());
        logger.debug ("parentId = " + dr.getParentId());
        logger.debug ("headId = " + dr.getHeadId());

    }

    @Test
    public void testUpdateDepartment() {
        String URI = BASE_URI + "update.department";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<DepartmentRecord> entity = new HttpEntity<>(origDepartment, headers);

        ResponseEntity<ServiceResult> response = restTemplate.exchange(URI, HttpMethod.PUT, entity, ServiceResult.class);
        ServiceResult sr = response.getBody();

        Assert.assertNotNull(sr);
        Assert.assertNotNull ("Result update.department is null!", sr);
        Assert.assertEquals ("update.department is not success! " + sr.getErrInfo (), true, sr.isSuccess ());
        Assert.assertTrue ("update.department must return not null identifer!", (sr.getId () != null && sr.getId () > 0));
        logger.debug ("The department is updated. id = " + sr.getId ());
    }

    @Test
    public void testAddWorker() {

        String URI = BASE_URI + "add.worker";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<WorkerRecord> entity = new HttpEntity<>(origWorker, headers);

        ResponseEntity<ServiceResult> response = restTemplate.exchange(URI, HttpMethod.POST, entity, ServiceResult.class);
        ServiceResult sr = response.getBody();

        Assert.assertNotNull ("Result add.worker is null!", sr);
        Assert.assertEquals ("add.worker is not success! " + sr.getErrInfo (), true, sr.isSuccess ());
        Assert.assertTrue ("add.worker must return not null identifer!", (sr.getId () != null && sr.getId () > 0));
        origWorker.setId (sr.getId ());
        logger.debug ("The worker is added. id = " + sr.getId ());
    }

    @Test
    public void testUpdateWorker() {
        String URI = BASE_URI + "update.worker";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<WorkerRecord> entity = new HttpEntity<>(origWorker, headers);

        ResponseEntity<ServiceResult> response = restTemplate.exchange(URI, HttpMethod.PUT, entity, ServiceResult.class);
        ServiceResult sr = response.getBody();

        Assert.assertNotNull ("Result update.worker is null!", sr);
        Assert.assertEquals ("update.worker is not success! " + sr.getErrInfo (), true, sr.isSuccess ());
        Assert.assertTrue ("update.worker must return not null identifer!", (sr.getId () != null && sr.getId () > 0));
        logger.debug ("The worker is updated. id = " + sr.getId ());
    }

    @Test
    public void testDeleteWorker() {

        String URI = BASE_URI + "delete.worker";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<WorkerRecord> entity = new HttpEntity<>(origWorker, headers);

        ResponseEntity<ServiceResult> response = restTemplate.exchange(URI, HttpMethod.DELETE, entity, ServiceResult.class);
        ServiceResult sr = response.getBody();

        Assert.assertNotNull ("Result delete.worker is null!", sr);
        Assert.assertEquals ("delete.worker is not success! " + sr.getErrInfo (), true, sr.isSuccess ());
        Assert.assertTrue ("delete.worker must return not null identifer!", (sr.getId () != null && sr.getId () > 0));
        logger.debug ("The worker is deleted. id = " + sr.getId ());
    }

    @Test
    public void testUpdatePhoto() {

        Long id = new Long (149);
        byte[] buf = read (id);
        logger.debug ("personId = " + id);
        logger.debug ("photo = " + buf);
        logger.debug("photo's length = " + (buf != null ? buf.length : null));

        String URI = BASE_URI + "update.photo";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<byte[]> entity = new HttpEntity<>(buf, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(URI).queryParam("id", 148L);
        String uriBuilder = builder.build().encode().toUriString();

        ResponseEntity<ServiceResult> response = restTemplate.exchange(uriBuilder, HttpMethod.PUT, entity, ServiceResult.class);
        ServiceResult sr = response.getBody();

        Assert.assertNotNull ("Result updatePhoto() is null!", sr);
        Assert.assertEquals ("updatePhoto() is not success! " + sr.getErrInfo (), true, sr.isSuccess ());
        Assert.assertTrue ("updatePhoto() must return not null identifer!", (sr.getId () != null && sr.getId () > 0));
        File newFile = new File (WSConfig.getInstance ().getDirPhotos () + sr.getId () + ".jpg");
        Assert.assertEquals ("New file not exist", true, (newFile.exists () && (newFile.length () > 0)));
        logger.debug ("The photo of worker is updated. id = " + sr.getId ());
    }

    private byte[] read(Long id) {

        ByteArrayOutputStream out = null;
        InputStream input = null;

        try {
            String fileName = dirPhotos + id + ".jpg";
            logger.debug("=== fileName = " + fileName);
            File file = new File(fileName);
            if (file.exists()) {

                out = new ByteArrayOutputStream();
                input = new BufferedInputStream(new FileInputStream(file));
                int data = 0;
                while ((data = input.read()) != -1){
                    out.write(data);
                }
                logger.debug("=== file exists");
            } else {
                logger.debug ("=== file doesn't exist");
            }
        } catch (Exception e) {
            logger.error ("error while update photo", e);
        }
        finally{
            try {
                input.close();
                out.close ();
            } catch (Exception e) {}
        }
        return out.toByteArray();
    }

    @Test
    public void testDeleteDepartment() {

        String URI = BASE_URI + "delete.department";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<DepartmentRecord> entity = new HttpEntity<>(origDepartment, headers);

        ResponseEntity<ServiceResult> response = restTemplate.exchange(URI, HttpMethod.DELETE, entity, ServiceResult.class);
        ServiceResult sr = response.getBody();

        Assert.assertNotNull ("Result delete.department is null!", sr);
        Assert.assertEquals ("delete.department is not success! " + sr.getErrInfo (), true, sr.isSuccess ());
        Assert.assertTrue ("delete.department must return not null identifer!", (sr.getId () != null && sr.getId () > 0));
        logger.debug ("The department is deleted. id = " + sr.getId ());
    }

    private List<HttpMessageConverter<?>> getMessageConverters() {
        Jaxb2Marshaller oxmMarshaller = new Jaxb2Marshaller();
        oxmMarshaller.setClassesToBeBound(WorkerRecord.class, WorkerRecordList.class,
                DepartmentRecord.class, ServiceResult.class, ServiceResultList.class);
        MarshallingHttpMessageConverter marshallingConverter = new MarshallingHttpMessageConverter(oxmMarshaller);

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(marshallingConverter);
        converters.add(new ByteArrayHttpMessageConverter());

/*
        XStreamMarshaller xstreamMarshaller = new XStreamMarshaller();
        MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter(xstreamMarshaller);

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(xmlConverter);
        converters.add(new MappingJackson2HttpMessageConverter());
*/
        return converters;
    }
}
