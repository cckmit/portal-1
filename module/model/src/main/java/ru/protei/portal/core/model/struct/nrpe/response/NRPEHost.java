package ru.protei.portal.core.model.struct.nrpe.response;

public abstract class NRPEHost extends NRPERaw {
    protected String ipTarget;
    protected String ipSource;
    protected int probes;
    protected int broadcast;
    protected int response;

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
}