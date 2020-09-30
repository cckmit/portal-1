package ru.protei.portal.core.nrpe.response;

import ru.protei.portal.core.nrpe.NRPEResponse;
import ru.protei.portal.core.nrpe.NRPEStatus;

public class NRPEHostUnreachable implements NRPEResponse {
    private final String ipTarget;
    private final String ipSource;
    private final int probes;
    private final int broadcast;
    private final int response;

    public NRPEHostUnreachable(String ipTarget, String ipSource, int probes, int broadcast, int response) {
        this.ipTarget = ipTarget;
        this.ipSource = ipSource;
        this.probes = probes;
        this.broadcast = broadcast;
        this.response = response;
    }

    public String getIpTarget() {
        return ipTarget;
    }

    public String getIpSource() {
        return ipSource;
    }

    public int getProbes() {
        return probes;
    }

    public int getBroadcasts() {
        return broadcast;
    }

    public int getResponses() {
        return response;
    }

    @Override
    public NRPEStatus getNRPEStatus() {
        return NRPEStatus.HOST_UNREACHABLE;
    }
}
