package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;
import ru.protei.portal.core.service.JiraStatusService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.JiraStatusController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("JiraStatusController")
public class JiraStatusControllerImpl implements JiraStatusController {
    @Override
    public List<JiraStatusMapEntry> getJiraStatusMapEntryList() throws RequestFailedException {
        log.info("getJiraStatusMapEntryList()");

        Result<List<JiraStatusMapEntry>> result = jiraStatusService.getJiraStatusMapEntryList(ServiceUtils.getAuthToken(sessionService, httpServletRequest));

        if (result.isError()) {
            log.info("getJiraStatusMapEntryList() :: result: error. status={}", result.getStatus());
            throw new RequestFailedException(result.getStatus());
        }

        log.info("getJiraStatusMapEntryList() :: result: ok. data={}", result.getData());
        return result.getData();
    }

    @Autowired
    JiraStatusService jiraStatusService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    SessionService sessionService;

    private static final Logger log = LoggerFactory.getLogger(JiraStatusControllerImpl.class);
}
