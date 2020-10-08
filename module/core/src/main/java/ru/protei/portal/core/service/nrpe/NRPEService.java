package ru.protei.portal.core.service.nrpe;

import ru.protei.portal.core.nrpe.NRPEResponse;

/**
 * Сервис NRPE (опрос ip)
 */
public interface NRPEService {
    NRPEResponse checkIp(String ip);
}
