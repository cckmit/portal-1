package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dict.En_CaseLink;

import java.util.HashMap;
import java.util.Map;

public class CaseLinkServiceImpl implements CaseLinkService {

    @Autowired
    PortalConfig portalConfig;

    @Override
    public CoreResponse<Map<En_CaseLink, String>> getLinkMap() {
        Map<En_CaseLink, String> linkMap = new HashMap<>();
        linkMap.put(En_CaseLink.CRM, portalConfig.data().getCaseLinkConfig().getLinkCrm());
        linkMap.put(En_CaseLink.CRM_OLD, portalConfig.data().getCaseLinkConfig().getLinkOldCrm());
        linkMap.put(En_CaseLink.YT, portalConfig.data().getCaseLinkConfig().getLinkYouTrack());
        return new CoreResponse<Map<En_CaseLink, String>>().success(linkMap);
    }
}
