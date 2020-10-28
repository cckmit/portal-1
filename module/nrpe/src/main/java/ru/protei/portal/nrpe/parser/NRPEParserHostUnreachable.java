package ru.protei.portal.nrpe.parser;

import ru.protei.portal.core.model.struct.nrpe.response.NRPEResponse;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEHostUnreachable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class NRPEParserHostUnreachable extends NRPEParserHost {
    static public NRPEResponse parse(List<String> content) {
        if (content.size() != 3) {
            return null;
        }
        Matcher matcher0 = lineWelcome.matcher(content.get(0));
        Matcher matcher1 = lineProbes.matcher(content.get(1));
        Matcher matcher2 = lineResponses.matcher(content.get(2));
        if (matcher0.find() && matcher1.find() && matcher2.find()) {
            return new NRPEHostUnreachable(
                    new ArrayList<>(content),
                    matcher0.group("ipTarget"),
                    matcher0.group("ipSource"),
                    Integer.parseInt(matcher1.group("probes")),
                    Integer.parseInt(matcher1.group("broadcasts")),
                    Integer.parseInt(matcher2.group("responses")));
        }
        return null;
    }
}
