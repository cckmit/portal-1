package ru.protei.portal.core.nrpe.response;

import ru.protei.portal.core.nrpe.NRPEResponse;
import ru.protei.portal.core.nrpe.NRPEStatus;

public class NRPEServerUnavailable implements NRPEResponse {
    private String ip;
    private String port;
    private String host;

    public NRPEServerUnavailable(String ip, String port, String host) {
        this.ip = ip;
        this.port = port;
        this.host = host;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    @Override
    public NRPEStatus getNRPEStatus() {
        return NRPEStatus.SERVER_UNAVAILABLE;
    }
}