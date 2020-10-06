package ru.protei.portal.core.nrpe.parser;

import java.util.regex.Pattern;

public abstract class NRPEParserHost implements NRPEParser {
    static protected final String IP_TEMPLATE = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    static protected final String MAC_TEMPLATE = "([0-9A-F]{2}[:-]){5}([0-9A-F]{2})";
    static protected final Pattern lineWelcome = Pattern.compile("^ARPING (?<ipTarget>" + IP_TEMPLATE + ") from (?<ipSource>" + IP_TEMPLATE + ") lan$");
    static protected final Pattern lineProbes = Pattern.compile("^Sent (?<probes>\\d+) probes \\((?<broadcasts>\\d+) broadcast\\(s\\)\\)$");
    static protected final Pattern lineResponses = Pattern.compile("^Received (?<responses>\\d+) response\\(s\\)$");
}
