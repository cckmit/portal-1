package ru.protei.portal.core.nrpe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.nrpe.parser.NRPEParserHostReachable;
import ru.protei.portal.core.nrpe.parser.NRPEParserHostUnreachable;
import ru.protei.portal.core.nrpe.parser.NRPEParserIncorrectParams;
import ru.protei.portal.core.nrpe.parser.NRPEParserServerUnavailable;

import java.util.List;
import java.util.Objects;

public class NRPERequest {
    private static Logger log = LoggerFactory.getLogger(NRPERequest.class);
    @Autowired
    public NRPERequest(NRPEExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    PortalConfig portalConfig;

    private final NRPEExecutor executor;

    public NRPEResponse perform(String ip) {
        return perform(ip, portalConfig.data().getNrpeConfig().getTemplate());
    }

    public NRPEResponse perform(String ip, String template) {
        if (ip == null) {
            log.error("ip == null");
            return null;
        }
        String request = String.format(template, ip);
        log.info("request: {}", request);
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

        NRPEStatus status;
        try {
            status = NRPEStatus.find(Integer.parseInt(list.get(list.size() - 1)));
        } catch (NumberFormatException exception) {
            log.error("status parse error, status = {}", list.get(list.size() - 1));
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
                log.error("no parser for status, status = {}", status);
                return null;
        }
    }
}
