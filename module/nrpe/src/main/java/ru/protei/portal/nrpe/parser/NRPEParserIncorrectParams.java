package ru.protei.portal.nrpe.parser;

import ru.protei.portal.core.model.struct.nrpe.response.NRPEResponse;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEIncorrectParams;

import java.util.List;

public class NRPEParserIncorrectParams implements NRPEParser {
    static public NRPEResponse parse(List<String> content) {
        return new NRPEIncorrectParams(String.join("\n", content));
    }
}
