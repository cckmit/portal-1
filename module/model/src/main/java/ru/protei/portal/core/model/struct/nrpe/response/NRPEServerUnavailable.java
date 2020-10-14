package ru.protei.portal.core.model.struct.nrpe.response;

import ru.protei.portal.core.model.dict.En_NRPEStatus;

public class NRPEServerUnavailable implements NRPEResponse {
    private final String ip;
    private final String port;
    private final String host;

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
    public En_NRPEStatus getNRPEStatus() {
        return En_NRPEStatus.SERVER_UNAVAILABLE;
    }
}