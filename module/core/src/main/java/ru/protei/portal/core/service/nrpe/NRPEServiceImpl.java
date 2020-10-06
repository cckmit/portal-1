package ru.protei.portal.core.service.nrpe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.nrpe.NRPERequest;
import ru.protei.portal.core.nrpe.NRPEResponse;
import ru.protei.portal.core.nrpe.NRPEStatus;

import static ru.protei.portal.core.model.helper.HelperFunc.isEmpty;

public class NRPEServiceImpl implements NRPEService {
    private static Logger log = LoggerFactory.getLogger(NRPEServiceImpl.class);

    @Autowired
    NRPERequest nrpeRequest;

    @Override
    public Boolean isIpAvailable(String ip) {
        if (isEmpty(ip)) {
            return null;
        }

        NRPEResponse response = nrpeRequest.perform(ip);
        if (response == null) {
            return null;
        }

        return response.getNRPEStatus() == NRPEStatus.HOST_UNREACHABLE;
    }
}
