package ru.protei.portal.core.nrpe.response;

import ru.protei.portal.core.nrpe.NRPEStatus;

public class NRPEHostUnreachable extends NRPEHost {
    public NRPEHostUnreachable(String ipTarget, String ipSource, int probes, int broadcast, int response) {
        this.ipTarget = ipTarget;
        this.ipSource = ipSource;
        this.probes = probes;
        this.broadcast = broadcast;
        this.response = response;
    }

    @Override
    public NRPEStatus getNRPEStatus() {
        return NRPEStatus.HOST_UNREACHABLE;
    }
}
