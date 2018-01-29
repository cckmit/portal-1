package ru.protei.portal.test.api;

import junit.framework.Assert;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.protei.portal.api.config.WSConfig;
import ru.protei.portal.api.model.DepartmentRecord;
import ru.protei.portal.api.model.Photo;
import ru.protei.portal.api.model.ServiceResult;
import ru.protei.portal.api.model.WorkerRecord;
import ru.protei.portal.api.service.WorkerService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by turik on 02.09.16.
 */
public class TestSoapService {

    private static Logger logger = Logger.getLogger(TestSoapService.class);

    private static JaxWsProxyFactoryBean factory;
    private static WorkerService client;

    private static String spAddress;
    private static String dirPhotos;
    private static DepartmentRecord origDepartment = new DepartmentRecord ();
    private static WorkerRecord origWorker = new WorkerRecord ();
    private static ServiceResult result = new ServiceResult ();

    @BeforeClass
    public static void setUpClass() {

        InputStream is = null;

        try {

            is = TestSoapService.class.getResourceAsStream("/service.properties");
            Properties props = new Properties();
            props.load(is);

            spAddress = props.getProperty ("service_publish_address");
            dirPhotos = props.getProperty ("dir_photos");

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
            origWorker.setFired (new Boolean (props.getProperty ("isFired")));
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

        factory = new JaxWsProxyFactoryBean();
        factory.getServiceFactory().setDataBinding(new AegisDatabinding ());
        factory.setServiceClass(WorkerService.class);
        factory.setAddress(spAddress);
        client = (WorkerService) factory.create ();

    }

    @Test
    public void testGetWorker() {

        WorkerRecord wr = client.getWorker (new Long (148));
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
    public void testGetWorkers() {

        List<WorkerRecord> list = client.getWorkers ("Турик");
        Assert.assertNotNull ("Result of getWorkers() is null!", list);
        for (WorkerRecord wr : list) {
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
            logger.debug ("==============================");
        }
    }

    @Test
    public void testUpdateDepartment() {

        result = client.updateDepartment (origDepartment);
        Assert.assertNotNull ("Result updateDepartment() is null!", result);
        Assert.assertEquals ("updateDepartment() is not success! " + result.getErrInfo (), true, result.isSuccess ());
        Assert.assertTrue ("updateDepartment() must return not null identifer!", (result.getId () != null && result.getId () > 0));
        logger.debug ("The department is updated. id = " + result.getId ());
    }

    @Test
    public void testAddWorker() {

        result = client.addWorker (origWorker);
        Assert.assertNotNull ("Result addWorker() is null!", result);
        Assert.assertEquals ("addWorker() is not success! " + result.getErrInfo (), true, result.isSuccess ());
        Assert.assertTrue ("addWorker() must return not null identifer!", (result.getId () != null && result.getId () > 0));
        origWorker.setId (result.getId ());
        logger.debug ("The worker is added. id = " + result.getId ());
    }

    @Test
    public void testGetAddedWorker() {

        WorkerRecord wr = client.getWorker (origWorker.getId ());
        Assert.assertNotNull ("Result of getWorker() is null!", wr);
        Assert.assertEquals ("addWorker(): error while compare firstName! ", origWorker.getFirstName (), wr.getFirstName ());
        Assert.assertEquals ("addWorker(): error while compare lastName! ", origWorker.getLastName (), wr.getLastName ());
        Assert.assertEquals ("addWorker(): error while compare secondName! ", origWorker.getSecondName () != null ? origWorker.getSecondName () : "", wr.getSecondName () != null ? wr.getSecondName () : "");
        Assert.assertEquals ("addWorker(): error while compare sex! ", origWorker.getSex (), wr.getSex ());
        Assert.assertEquals ("addWorker(): error while compare birthday! ", origWorker.getBirthday () != null ? origWorker.getBirthday () : "", wr.getBirthday () != null ? wr.getBirthday () : "");
        Assert.assertEquals ("addWorker(): error while compare phoneWork! ", origWorker.getPhoneWork () != null ? origWorker.getPhoneWork () : "", wr.getPhoneWork () != null ? wr.getPhoneWork () : "");
        Assert.assertEquals ("addWorker(): error while compare phoneHome! ", origWorker.getPhoneHome () != null ? origWorker.getPhoneHome () : "", wr.getPhoneHome () != null ? wr.getPhoneHome () : "");
        Assert.assertEquals ("addWorker(): error while compare phoneMobile! ", origWorker.getPhoneMobile () != null ? origWorker.getPhoneMobile () : "" , wr.getPhoneMobile () != null ? wr.getPhoneMobile () : "");
        Assert.assertEquals ("addWorker(): error while compare email! ", origWorker.getEmail () != null ? origWorker.getEmail () : "", wr.getEmail () != null ? wr.getEmail () : "");
        Assert.assertEquals ("addWorker(): error while compare emailOwn! ", origWorker.getEmailOwn () != null ? origWorker.getEmailOwn () : "", wr.getEmailOwn () != null ? wr.getEmailOwn () : "");
        Assert.assertEquals ("addWorker(): error while compare fax! ", origWorker.getFax () != null ? origWorker.getFax () : "", wr.getFax () != null ? wr.getFax () : "");
        Assert.assertEquals ("addWorker(): error while compare address! ", origWorker.getAddress () != null ? origWorker.getAddress () : "", wr.getAddress () != null ? wr.getAddress () : "");
        Assert.assertEquals ("addWorker(): error while compare addressHome! ", origWorker.getAddressHome () != null ? origWorker.getAddressHome () : "", wr.getAddressHome () != null ? wr.getAddressHome () : "");
        Assert.assertEquals ("addWorker(): error while compare passportInfo! ", origWorker.getPassportInfo () != null ? origWorker.getPassportInfo () : "", wr.getPassportInfo () != null ? wr.getPassportInfo () : "");
        Assert.assertEquals ("addWorker(): error while compare info! ", origWorker.getInfo () != null ? origWorker.getInfo () : "", wr.getInfo () != null ? wr.getInfo () : "");
        Assert.assertEquals ("addWorker(): error while compare ipAddress! ", origWorker.getIpAddress () != null ? origWorker.getIpAddress () : "", wr.getIpAddress () != null ? wr.getIpAddress () : "");
        Assert.assertEquals ("addWorker(): error while compare isDeleted! ", origWorker.isDeleted (), wr.isDeleted ());
        logger.debug ("The added worker is received.");
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
    public void testUpdateWorker() {
        result = client.updateWorker (origWorker);
        Assert.assertNotNull ("Result updateWorker() is null!", result);
        Assert.assertEquals ("updateWorker() is not success! " + result.getErrInfo (), true, result.isSuccess ());
        Assert.assertTrue ("updateWorker() must return not null identifer!", (result.getId () != null && result.getId () > 0));
        logger.debug ("The worker is updated. id = " + result.getId ());
    }

    @Test
    public void testUpdateWorkers() {

        List<WorkerRecord> list = new ArrayList<> ();
        list.add (origWorker);

        List<ServiceResult> results = client.updateWorkers (list);

        Assert.assertNotNull ("Results updateWorkers() is null!", results);

        for (ServiceResult result : results) {
            Assert.assertNotNull ("Result updateWorkers() is null!", result);
            Assert.assertEquals ("updateWorkers() is not success! " + result.getErrInfo (), true, result.isSuccess ());
            Assert.assertTrue ("updateWorkers() must return not null identifer!", (result.getId () != null && result.getId () > 0));
            logger.debug ("The worker is updated. id = " + result.getId ());
        }
    }

    @Test
    public void testUpdatePhoto() {

        Long id = new Long (149);
        byte[] buf = read (id);
        logger.debug ("personId = " + id);
        logger.debug ("photo = " + buf);
        logger.debug("photo's length = " + (buf != null ? buf.length : null));

        result = client.updatePhoto (id, buf);
        Assert.assertNotNull ("Result updatePhoto() is null!", result);
        Assert.assertEquals ("updatePhoto() is not success! " + result.getErrInfo (), true, result.isSuccess ());
        Assert.assertTrue ("updatePhoto() must return not null identifer!", (result.getId () != null && result.getId () > 0));
        File newFile = new File (WSConfig.getInstance ().getDirPhotos () + result.getId () + ".jpg");
        Assert.assertEquals ("New file not exist", true, (newFile.exists () && (newFile.length () > 0)));
        logger.debug ("The photo of worker is updated. id = " + result.getId ());
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
    public void testGetPhotos() {
        List<Long> list = new ArrayList<> ();
        list.add (new Long (148));
        list.add (new Long (149));
        List<Photo> result = client.getPhotos (list);
        Assert.assertNotNull ("Result getPhotos() is null!", result);
        for (Photo p : result) {
            Assert.assertNotNull ("Photo for id = " + p.getId () + " not exist!", p.getContent ());
            logger.debug ("Photo for id = " + p.getId () + " exist. Length of photo = " + p.getContent ().length());
        }
    }

    @Test
    public void testDeleteWorker() {

        result = client.deleteWorker (origWorker);
        Assert.assertNotNull ("Result deleteWorker() is null!", result);
        Assert.assertEquals ("deleteWorker() is not success! " + result.getErrInfo (), true, result.isSuccess ());
        Assert.assertTrue ("deleteWorker() must return not null identifer!", (result.getId () != null && result.getId () > 0));
        logger.debug ("The worker is deleted. id = " + result.getId ());
    }

    @Test
    public void testDeleteDepartment() {

        result = client.deleteDepartment (origDepartment);
        Assert.assertNotNull ("Result deleteDepartment() is null!", result);
        Assert.assertEquals ("deleteDepartment() is not success! " + result.getErrInfo (), true, result.isSuccess ());
        Assert.assertTrue ("deleteDepartment() must return not null identifer!", (result.getId () != null && result.getId () > 0));
        logger.debug ("The department is deleted. id = " + result.getId ());
    }

}
