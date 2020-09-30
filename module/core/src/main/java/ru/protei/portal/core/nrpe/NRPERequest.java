package ru.protei.portal.core.nrpe;

import ru.protei.portal.core.nrpe.parser.NRPEParserHostReachable;
import ru.protei.portal.core.nrpe.parser.NRPEParserHostUnreachable;
import ru.protei.portal.core.nrpe.parser.NRPEParserIncorrectParams;
import ru.protei.portal.core.nrpe.parser.NRPEParserServerUnavailable;

import java.util.List;
import java.util.Objects;

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

        switch (Objects.requireNonNull(status)) {
            case HOST_REACHABLE:
                return NRPEParserHostReachable.parse(content);
            case HOST_UNREACHABLE:
                return NRPEParserHostUnreachable.parse(content);
            case SERVER_UNAVAILABLE:
                return NRPEParserServerUnavailable.parse(content);
            case INCORRECT_PARAMS:
                return NRPEParserIncorrectParams.parse(content);
            default:
                return null;
        }
    }
}
