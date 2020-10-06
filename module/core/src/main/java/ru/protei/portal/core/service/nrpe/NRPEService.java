package ru.protei.portal.core.service.nrpe;

/**
 * Сервис NRPE (опрос ip)
 */
public interface NRPEService {
    Boolean isIpAvailable(String ip);
}
