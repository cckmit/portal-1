package ru.protei.portal.nrpe.parser;

import ru.protei.portal.core.model.struct.nrpe.response.NRPEResponse;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEServerUnavailable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NRPEParserServerUnavailable implements NRPEParser {
    static private final String IP_TEMPLATE = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    static private final Pattern line0 = Pattern.compile("^connect to address (?<ip>" + IP_TEMPLATE + ") port (?<port>\\d+): Connection refused$");
    static private final Pattern line1 = Pattern.compile("^connect to host (?<host>\\w+\\.\\w+\\.\\w+) port \\d+: Connection refused$");

    static public NRPEResponse parse(List<String> content) {
        if (content.size() != 2) {
            return null;
        }
        Matcher matcher0 = line0.matcher(content.get(0));
        Matcher matcher1 = line1.matcher(content.get(1));
        if (matcher0.find() && matcher1.find()) {
            return new NRPEServerUnavailable(matcher0.group("ip"), matcher0.group("port"), matcher1.group("host"));
        }
        return null;
    }
}

