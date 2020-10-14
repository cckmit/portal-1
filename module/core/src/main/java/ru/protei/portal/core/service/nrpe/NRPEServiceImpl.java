package ru.protei.portal.core.service.nrpe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.nrpe.NRPEProcessor;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEResponse;

import static ru.protei.portal.core.model.helper.HelperFunc.isEmpty;

public class NRPEServiceImpl implements NRPEService {
    private static Logger log = LoggerFactory.getLogger(NRPEServiceImpl.class);

    @Autowired
    NRPEProcessor nrpeProcessor;

    @Autowired
    PortalConfig portalConfig;

    @Override
    public NRPEResponse checkIp(String ip) {
        log.info("NRPEServiceImpl: checkIp ip={}", ip);
        if (isEmpty(ip)) {
            return null;
        }

        return nrpeProcessor.request(ip, portalConfig.data().getNrpeConfig().getTemplate());
    }
}
