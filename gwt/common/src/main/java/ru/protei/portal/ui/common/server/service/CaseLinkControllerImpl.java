package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.ui.common.client.service.CaseLinkController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.Map;

@Service("CaseLinkController")
public class CaseLinkControllerImpl implements CaseLinkController {

    @Override
    public Map<En_CaseLink, String> getLinkMap() throws RequestFailedException {
        log.debug("get case link map");

        CoreResponse<Map<En_CaseLink, String>> response = caseLinkService.getLinkMap();

        log.debug("get case link map -> {} ", response.isOk() ? "ok" : response.getStatus());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.isOk() ? response.getData() : null;
    }

    @Autowired
    ru.protei.portal.core.service.CaseLinkService caseLinkService;

    private static final Logger log = LoggerFactory.getLogger("web");
}
