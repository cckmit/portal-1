package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseObjectTagDAO;
import ru.protei.portal.core.model.dao.CaseTagDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObjectTag;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;

import java.util.Collections;
import java.util.List;

public class CaseTagServiceImpl implements CaseTagService {

    @Override
    public CoreResponse createTag(AuthToken token, CaseTag caseTag) {
        if (!isCaseTagValid(caseTag)) {
            return new CoreResponse<>().error(En_ResultStatus.INCORRECT_PARAMS);
        }
        Long id;
        try {
            id = caseTagDAO.persist(caseTag);
        } catch (DuplicateKeyException exception) {
            return new CoreResponse<>().error(En_ResultStatus.ALREADY_EXIST);
        }
        return id == null ?
                new CoreResponse<>().error(En_ResultStatus.NOT_CREATED) :
                new CoreResponse<>().success();
    }

    @Override
    public CoreResponse<List<CaseObjectTag>> getTagsForCase(AuthToken token, long caseId) {
        List<CaseObjectTag> tags = caseObjectTagDAO.getListByCaseId(caseId);
        return new CoreResponse<List<CaseObjectTag>>().success(tags);
    }

    @Override
    public CoreResponse<List<CaseTag>> getTagList(AuthToken token, En_CaseType caseType) {
        CaseTagQuery query = new CaseTagQuery();
        query.setCaseTypes(Collections.singletonList(caseType));
        List<CaseTag> caseTags = caseTagDAO.getListByQuery(query);
        return new CoreResponse<List<CaseTag>>().success(caseTags);
    }

    private boolean isCaseTagValid(CaseTag caseTag) {
        if (caseTag == null) {
            return false;
        }
        if (caseTag.getCaseType() == null) {
            return false;
        }
        if (StringUtils.isBlank(caseTag.getName())) {
            return false;
        }
        return true;
    }

    @Autowired
    CaseTagDAO caseTagDAO;
    @Autowired
    CaseObjectTagDAO caseObjectTagDAO;
}
