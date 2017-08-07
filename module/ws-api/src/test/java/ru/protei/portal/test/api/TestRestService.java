package ru.protei.portal.test.api;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.model.DepartmentRecord;
import ru.protei.portal.api.model.ServiceResult;
import ru.protei.portal.api.model.WorkerRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestRestService {
    private static Logger logger = Logger.getLogger(TestRestService.class);

    String BASE_URI = "http://localhost:8090/api/worker/";

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

        Assert.assertNotNull ("Result of getWorker() is null!", wr);
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

        ResponseEntity<DepartmentRecord> response =
                restTemplate.exchange(URI, HttpMethod.GET, entity, DepartmentRecord.class);
        DepartmentRecord resource = response.getBody();

        Assert.assertNotNull(resource);
        logger.debug(resource.getDepartmentName());
    }

    private List<HttpMessageConverter<?>> getMessageConverters() {
/*
        Jaxb2Marshaller oxmMarshaller = new Jaxb2Marshaller();
        oxmMarshaller.setClassesToBeBound(WorkerRecord.class, DepartmentRecord.class, ServiceResult.class);
        MarshallingHttpMessageConverter marshallingConverter = new MarshallingHttpMessageConverter(oxmMarshaller);

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(marshallingConverter);
*/

        XStreamMarshaller xstreamMarshaller = new XStreamMarshaller();
        MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter(xstreamMarshaller);

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(xmlConverter);
        converters.add(new MappingJackson2HttpMessageConverter());
        return converters;
    }
}
