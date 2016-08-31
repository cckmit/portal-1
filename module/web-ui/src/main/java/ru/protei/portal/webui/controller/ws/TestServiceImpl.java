package ru.protei.portal.webui.controller.ws;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import ru.protei.portal.core.model.dao.PersonDAO;

import javax.jws.WebService;
import java.util.Date;

/**
 * Created by michael on 30.08.16.
 */
@WebService(endpointInterface = "ru.protei.portal.webui.controller.ws.TestService")
public class TestServiceImpl implements TestService
{
    private static Logger logger = Logger.getLogger(TestServiceImpl.class);

    @Autowired
    PersonDAO personDAO;

    public String hello () {
        return "Hello World! It's now : " + new Date().toString();
    }


    @Scheduled(fixedRate = 3 * 1000, initialDelay = 5000)
    public void selfTest () {
        logger.debug("Self-Test SOAP API, person DAO wired: " + personDAO);
    }



}
