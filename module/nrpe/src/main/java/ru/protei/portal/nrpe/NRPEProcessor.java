package ru.protei.portal.nrpe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dict.En_NRPEStatus;
import ru.protei.portal.core.model.struct.nrpe.response.NRPEResponse;
import ru.protei.portal.nrpe.parser.NRPEParserHostReachable;
import ru.protei.portal.nrpe.parser.NRPEParserHostUnreachable;
import ru.protei.portal.nrpe.parser.NRPEParserIncorrectParams;
import ru.protei.portal.nrpe.parser.NRPEParserServerUnavailable;

import java.util.List;
import java.util.Objects;

public class NRPEProcessor {
    private static Logger log = LoggerFactory.getLogger(NRPEProcessor.class);

    public NRPEProcessor(NRPEExecutor executor) {
        this.executor = executor;
    }

    private final NRPEExecutor executor;

    public NRPEResponse request(String ip, String template) {
        if (ip == null) {
            log.error("ip == null");
            return null;
        }
        String request = String.format(template, ip);
        log.info("request: {}", request);
        if (!validateRequest(request)) {
            log.info("validate: fail");
            return null;
        }

        List<String> list = executor.execute(request);
        if (list == null) {
            log.error("executor error");
        }
        return parse(list);
    }

    static public NRPEResponse parse(List<String> list) {
        if (list == null || list.size() <= 1) {
            log.error("list == null || list.size() <= 1");
            return null;
        }

        int statusLineIndex = list.size() - 1;
        String statusLine = list.get(statusLineIndex);

        En_NRPEStatus status;
        try {
            status = En_NRPEStatus.find(Integer.parseInt(statusLine));
        } catch (NumberFormatException exception) {
            log.error("status parse error, status = {}", statusLine);
            return null;
        }
        List<String> content = list.subList(0, statusLineIndex);

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
                log.error("no parser for status, status = {}", status);
                return null;
        }
    }

    static private boolean validateRequest(String request) {
        return request.contains("check_nrpe") && request.endsWith("; echo $?");
    }
}
