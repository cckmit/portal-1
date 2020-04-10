package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.service.SLAService;
import ru.protei.portal.core.service.SiteFolderService;
import ru.protei.portal.core.service.session.SessionService;
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

    @Override
    public List<ProjectSla> getSlaByPlatformId(Long platformId) throws RequestFailedException {
        log.info("getSlaByPlatformId: platformId={}", platformId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Platform> platformResponse = siteFolderService.getPlatform(token, platformId);

        if (platformResponse.isError()) {
            log.info("getSlaByPlatformId: status={}", platformResponse.getStatus());
            throw new RequestFailedException(platformResponse.getStatus());
        }

        Result<List<ProjectSla>> slaResponse = slaService.getProjectSlaByProjectId(token, platformResponse.getData().getProjectId());

        if (slaResponse.isError()) {
            log.info("getSlaByPlatformId: status={}", slaResponse.getStatus());
            throw new RequestFailedException(slaResponse.getStatus());
        }

        return slaResponse.getData();
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    SLAService slaService;
    @Autowired
    SiteFolderService siteFolderService;

    private static final Logger log = LoggerFactory.getLogger(SLAControllerImpl.class);
}
