package ru.protei.portal.core.nrpe.parser;

import ru.protei.portal.core.nrpe.NRPEResponse;
import ru.protei.portal.core.nrpe.response.NRPEHostUnreachable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NRPEParserHostUnreachable implements NRPEParser {
    static private final String IP_TEMPLATE = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    static private final Pattern line0 = Pattern.compile("^ARPING (?<ipTarget>" + IP_TEMPLATE + ") from (?<ipSorce>" + IP_TEMPLATE + ") lan$");
    static private final Pattern line1 = Pattern.compile("^Sent (?<probes>\\d+) probes \\((?<broadcasts>\\d+) broadcast\\(s\\)\\)$");
    static private final Pattern line2 = Pattern.compile("^Received (?<responses>\\d+) response\\(s\\)$");

    static public NRPEResponse parse(List<String> content) {
        if (content.size() != 3) {
            return null;
        }
        Matcher matcher0 = line0.matcher(content.get(0));
        Matcher matcher1 = line1.matcher(content.get(1));
        Matcher matcher2 = line2.matcher(content.get(2));
        if (matcher0.find() && matcher1.find() && matcher2.find()) {
            return new NRPEHostUnreachable(
                    matcher0.group("ipTarget"),
                    matcher0.group("ipSorce"),
                    Integer.parseInt(matcher1.group("probes")),
                    Integer.parseInt(matcher1.group("broadcasts")),
                    Integer.parseInt(matcher2.group("responses")));
        }
        return null;
    }
}
