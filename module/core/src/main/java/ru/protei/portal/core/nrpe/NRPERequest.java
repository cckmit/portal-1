package ru.protei.portal.core.nrpe;

import ru.protei.portal.core.nrpe.parser.NRPEParserIncorrectParams;
import ru.protei.portal.core.nrpe.parser.NRPEParserServerUnavailable;

import java.util.List;

public class NRPERequest {

    static public NRPEResponse parse(List<String> list) {
        if (list == null || list.size() <= 1) {
            return null;
        }

        NRPEStatus status;
        try {
            status = NRPEStatus.find(Integer.parseInt(list.get(list.size() - 1)));
        } catch (NumberFormatException exception) {
            return null;
        }
        List<String> content = list.subList(0, list.size() - 1);

        switch (status) {
            case SERVER_UNAVAILABLE:
                return NRPEParserServerUnavailable.parse(content);
            case INCORRECT_PARAMS:
                return NRPEParserIncorrectParams.parse(content);
            default:
                return null;
        }
    }
}
