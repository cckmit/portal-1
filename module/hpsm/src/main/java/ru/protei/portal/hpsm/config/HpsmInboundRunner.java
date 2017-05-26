package ru.protei.portal.hpsm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.protei.portal.hpsm.service.HpsmService;

/**
 * Created by michael on 16.05.17.
 */
@Component
public class HpsmInboundRunner {

    private static Logger logger = LoggerFactory.getLogger(HpsmInboundRunner.class);

    @Autowired
    HpsmService hpsmService;

    public HpsmInboundRunner() {
        logger.debug("HPSM incoming mail process runner - started");
    }


    @Scheduled(fixedRate = 30*1000)
    public void handle () {
        logger.debug("HPSM, check incoming messages - start");
        hpsmService.handleInboundRequest();
        logger.debug("HPSM, check incoming messages - end");
    }

}
