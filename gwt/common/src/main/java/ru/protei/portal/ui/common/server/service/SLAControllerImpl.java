package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;
import ru.protei.portal.core.service.SLAService;
import ru.protei.portal.ui.common.client.service.SLAController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("SLAController")
public class SLAControllerImpl implements SLAController {

    @Override
    public List<JiraSLAMapEntry> getJiraSLAEntries(long mapId) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(slaService.getJiraSLAEntries(token, mapId));
    }

    @Override
    public JiraSLAMapEntry getJiraSLAEntry(long mapId, String issueType, String severity) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(slaService.getJiraSLAEntry(token, mapId, issueType, severity));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    SLAService slaService;
}
