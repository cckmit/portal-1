package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.UitsIssueInfo;

public interface UitsService {

    Result<UitsIssueInfo> getIssueInfo(Long issueId );

}
