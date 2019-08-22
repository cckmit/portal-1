package ru.protei.portal.test.api;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import ru.protei.portal.api.config.APIConfigurationContext;
import ru.protei.portal.api.model.DepartmentRecord;
import ru.protei.portal.api.model.WorkerRecord;
import ru.protei.winter.core.CoreConfigurationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {APIConfigurationContext.class})
public class TestWorkerController {
    @Autowired
    WebApplicationContext webApplicationContext;

    private static Logger logger = LoggerFactory.getLogger(TestRestService.class);

    private MockMvc mockMvc;
    private static String BASE_URI;
    private static WorkerRecord origWorker = new WorkerRecord ();
    private static DepartmentRecord origDepartment = new DepartmentRecord ();

    @BeforeClass
    public static void initClass() throws Exception {
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

            System.out.println(origWorker);
        } catch (Exception e) {
            logger.error ("Can not read config!", e);
        } finally {
            try {
                is.close ();
            } catch (Exception e) {}
        }


    }
    @Test
    public void testAddWorker() throws Exception {
    }
}