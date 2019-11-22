package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseObjectTagDAO;
import ru.protei.portal.core.model.dao.CaseTagDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.auth.AuthService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
public class CaseTagServiceImpl implements CaseTagService {

    @Override
    @Transactional
    public Result saveTag( AuthToken authToken, CaseTag caseTag) {
        if (!isCaseTagValid(caseTag)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }
        boolean result;
        try {
            if (caseTag.getId() != null && !Objects.equals(caseTagDAO.get(caseTag.getId()).getPersonId(), caseTag.getPersonId())) {
                return error(En_ResultStatus.PERMISSION_DENIED);
            }
            result = caseTagDAO.saveOrUpdate(caseTag);
        } catch (DuplicateKeyException exception) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }
        return !result ?
                Result.error( En_ResultStatus.NOT_CREATED) :
                ok();
    }

    @Override
    @Transactional
    public Result removeTag( AuthToken authToken, CaseTag caseTag) {
        if (caseTag.getId() != null && !Objects.equals(caseTagDAO.get(caseTag.getId()).getPersonId(), caseTag.getPersonId())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        return !caseTagDAO.remove(caseTag) ?
                Result.error( En_ResultStatus.NOT_REMOVED) :
                ok();
    }

    @Override
    public Result<List<CaseTag>> getTags(AuthToken token, CaseTagQuery query) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_VIEW ) ) {
            query.setCompanyId(descriptor.getCompany().getId());
        }

        List<CaseTag> caseTags = caseTagDAO.getListByQuery(query);

        return ok(caseTags);
    }

    @Override
    public Result attachTag(AuthToken authToken, Long caseId, Long tagId) {
        if ( caseId == null || tagId == null ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseTag caseTag = caseTagDAO.get(tagId);
        if ( caseTag == null ) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        // TODO: check already exist bundle
        CaseObjectTag cot = new CaseObjectTag(caseId, tagId);
        caseObjectTagDAO.persist(cot);

        return ok();
    }

    @Override
    public Result detachTag(AuthToken authToken, Long caseId, Long tagId) {
        if ( caseId == null || tagId == null ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseTag caseTag = caseTagDAO.get(tagId);
        if ( caseTag == null ) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        caseObjectTagDAO.removeByCaseIdAndTagId(caseId, tagId);
        return ok();
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
