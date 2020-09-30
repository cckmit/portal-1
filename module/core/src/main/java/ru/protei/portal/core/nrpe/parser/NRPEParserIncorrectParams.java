package ru.protei.portal.core.nrpe.parser;

import ru.protei.portal.core.nrpe.NRPEResponse;
import ru.protei.portal.core.nrpe.response.NRPEIncorrectParams;

import java.util.List;

public class NRPEParserIncorrectParams implements NRPEParser {
    static public NRPEResponse parse(List<String> content) {
        return new NRPEIncorrectParams(String.join("\n", content));
    }
}
