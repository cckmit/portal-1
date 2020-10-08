package ru.protei.portal.core.nrpe.response;

import ru.protei.portal.core.nrpe.NRPEStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NRPEHostReachable extends NRPEHost {
    public static class ProbeInfo {
        private final String ip;
        private final String mac;
        private final String time;

        public ProbeInfo(String ip, String mac, String time) {
            this.ip = ip;
            this.mac = mac;
            this.time = time;
        }

        public String getIp() {
            return ip;
        }

        public String getMac() {
            return mac;
        }

        public String getTime() {
            return time;
        }

        @Override
        public String toString() {
            return String.format("%s [%s] %s ", ip, mac, time);
        }
    }

    private final List<ProbeInfo> probeInfos;
    private final boolean isIpConflict;

    public NRPEHostReachable(String ipTarget, String ipSource, int probes, int broadcast, int response, List<ProbeInfo> probeInfos, boolean isIpConflict) {
        this.ipTarget = ipTarget;
        this.ipSource = ipSource;
        this.probes = probes;
        this.broadcast = broadcast;
        this.response = response;
        this.probeInfos = probeInfos;
        this.isIpConflict = isIpConflict;
    }

    public List<ProbeInfo> getProbeInfos() {
        return probeInfos;
    }

    public boolean isIpConflict() {
        return isIpConflict;
    }

    public List<String> ipsAndMacs() {
        if (probeInfos == null) {
            return null;
        }
        if (isIpConflict) {
            return probeInfos.stream().map(String::valueOf).collect(Collectors.toList());
        } else {
            return Collections.singletonList(String.valueOf(probeInfos.get(0)));
        }
    }

    @Override
    public NRPEStatus getNRPEStatus() {
        return NRPEStatus.HOST_REACHABLE;
    }
}
