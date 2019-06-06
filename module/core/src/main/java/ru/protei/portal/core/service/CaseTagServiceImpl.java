package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseObjectTagDAO;
import ru.protei.portal.core.model.dao.CaseTagDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.service.user.AuthService;

import java.util.*;

public class CaseTagServiceImpl implements CaseTagService {

    @Override
    @Transactional
    public CoreResponse createTag(AuthToken token, CaseTag caseTag) {
        if (!isCaseTagValid(caseTag)) {
            return new CoreResponse<>().error(En_ResultStatus.VALIDATION_ERROR);
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
    public CoreResponse<List<CaseTag>> getTagsByCaseId(AuthToken token, long caseId) {
        CaseTagQuery query = new CaseTagQuery();

        query.setCaseId(caseId);

        return getTagsByPermission(token, query);
    }

    @Override
    public CoreResponse<List<CaseTag>> getTagsByCaseType(AuthToken token, En_CaseType caseType) {
        CaseTagQuery query = new CaseTagQuery();

        query.setCaseTypes(Collections.singletonList(caseType));

        return getTagsByPermission(token, query);
    }

    private CoreResponse<List<CaseTag>> getTagsByPermission(AuthToken token, CaseTagQuery query){
        UserSessionDescriptor descriptor = authService.findSession( token );
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_VIEW ) ) {
            query.setCompanyId(descriptor.getCompany().getId());
        }

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
        if (caseTag.getCompanyId() == null){
            return false;
        }
        return true;
    }

    @Autowired
    CaseTagDAO caseTagDAO;
    @Autowired
    CaseObjectTagDAO caseObjectTagDAO;
    @Autowired
    AuthService authService;
    @Autowired
    PolicyService policyService;
}
