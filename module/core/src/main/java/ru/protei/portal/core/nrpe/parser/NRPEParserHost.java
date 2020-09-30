package ru.protei.portal.core.nrpe.parser;

import ru.protei.portal.core.nrpe.NRPEResponse;
import ru.protei.portal.core.nrpe.response.NRPEHostUnreachable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NRPEParserHost implements NRPEParser {
    static protected final String IP_TEMPLATE = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    static protected final String MAC_TEMPLATE = "([0-9A-F]{2}[:-]){5}([0-9A-F]{2})";
    static protected final Pattern lineWelcome = Pattern.compile("^ARPING (?<ipTarget>" + IP_TEMPLATE + ") from (?<ipSource>" + IP_TEMPLATE + ") lan$");
    static protected final Pattern lineProbes = Pattern.compile("^Sent (?<probes>\\d+) probes \\((?<broadcasts>\\d+) broadcast\\(s\\)\\)$");
    static protected final Pattern lineResponses = Pattern.compile("^Received (?<responses>\\d+) response\\(s\\)$");

    static private NRPEResponse parse(List<String> content) {
        if (content.size() != 3) {
            return null;
        }
        Matcher matcher0 = lineWelcome.matcher(content.get(0));
        Matcher matcher1 = lineProbes.matcher(content.get(1));
        Matcher matcher2 = lineResponses.matcher(content.get(2));
        if (matcher0.find() && matcher1.find() && matcher2.find()) {
            return new NRPEHostUnreachable(
                    matcher0.group("ipTarget"),
                    matcher0.group("ipSource"),
                    Integer.parseInt(matcher1.group("probes")),
                    Integer.parseInt(matcher1.group("broadcasts")),
                    Integer.parseInt(matcher2.group("responses")));
        }
        return null;
    }
}
