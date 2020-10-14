package ru.protei.portal.core.model.struct.nrpe.response;

import ru.protei.portal.core.model.dict.En_NRPEStatus;

public class NRPEHostUnreachable extends NRPEHost {
    public NRPEHostUnreachable(String ipTarget, String ipSource, int probes, int broadcast, int response) {
        this.ipTarget = ipTarget;
        this.ipSource = ipSource;
        this.probes = probes;
        this.broadcast = broadcast;
        this.response = response;
    }

    @Override
    public En_NRPEStatus getNRPEStatus() {
        return En_NRPEStatus.HOST_UNREACHABLE;
    }
}
