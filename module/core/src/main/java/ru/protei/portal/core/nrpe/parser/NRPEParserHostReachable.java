package ru.protei.portal.core.nrpe.parser;

import ru.protei.portal.core.nrpe.NRPEResponse;
import ru.protei.portal.core.nrpe.response.NRPEHostReachable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NRPEParserHostReachable extends NRPEParserHost {
    static private final Pattern lineProbeInfo = Pattern.compile("^Unicast reply from (?<ip>" + IP_TEMPLATE + ") \\[(?<mac>" + MAC_TEMPLATE + ")\\] (?<time>\\d+\\.\\d+ms)$");
    static public NRPEResponse parse(List<String> content) {
        if (content.size() <= 3) {
            return null;
        }
        Matcher matcherWelcome = lineWelcome.matcher(content.get(0));
        Matcher matcherProbes = lineProbes.matcher(content.get(content.size()-2));
        Matcher matcherResponses = lineResponses.matcher(content.get(content.size()-1));
        if (matcherWelcome.find() && matcherProbes.find() && matcherResponses.find()) {
            List<NRPEHostReachable.ProbeInfo> probeInfos = new ArrayList<>();
            Set<String> macs = new HashSet<>();
            int probes = Integer.parseInt(matcherProbes.group("probes"));
            for (int i = 1; i < content.size()-2; i++) {
                Matcher matcher = lineProbeInfo.matcher(content.get(i));
                if (matcher.find()) {
                    NRPEHostReachable.ProbeInfo probeInfo = new NRPEHostReachable.ProbeInfo(
                            matcher.group("ip"),
                            matcher.group("mac"),
                            matcher.group("time")
                    );
                    macs.add(probeInfo.getMac());
                    probeInfos.add(probeInfo);
                } else {
                    return null;
                }
            }

            return new NRPEHostReachable(
                    matcherWelcome.group("ipTarget"),
                    matcherWelcome.group("ipSource"),
                    probes,
                    Integer.parseInt(matcherProbes.group("broadcasts")),
                    Integer.parseInt(matcherResponses.group("responses")),
                    probeInfos,
                    macs.size() != 1
                    );
        }
        return null;
    }
}