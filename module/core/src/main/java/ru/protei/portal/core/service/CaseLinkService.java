package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseLink;

import java.util.Map;

public interface CaseLinkService {

    CoreResponse<Map<En_CaseLink, String>> getLinkMap();
}
