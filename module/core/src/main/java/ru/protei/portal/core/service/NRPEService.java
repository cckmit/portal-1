package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;

/**
 * Сервис NRPE (опрос ip)
 */
public interface NRPEService {
    Result<Boolean> isIpAvailable(String ip);
}
