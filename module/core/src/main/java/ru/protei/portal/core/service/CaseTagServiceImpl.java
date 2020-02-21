package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseObjectTagDAO;
import ru.protei.portal.core.model.dao.CaseTagDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.LockStrategy;

import java.util.List;
import java.util.Objects;

import java.util.concurrent.TimeUnit;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class CaseTagServiceImpl implements CaseTagService {

    @Autowired
    private CaseTagDAO caseTagDAO;
    @Autowired
    private CaseObjectTagDAO caseObjectTagDAO;
    @Autowired
    private PolicyService policyService;
    @Autowired
    private LockService lockService;


    @Override
    @Transactional
    public Result<Long> create( AuthToken authToken, CaseTag caseTag) {
        caseTag.setPersonId( authToken.getPersonId() );

        if (!isCaseTagValid(caseTag)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (caseTagDAO.isNameExist( caseTag.getName() )) {
            return error( En_ResultStatus.ALREADY_EXIST );
        }

        return ok( caseTagDAO.persist( caseTag ) );
    }

    @Override
    @Transactional
    public Result<Long> update( AuthToken authToken, CaseTag caseTag) {
        if (!isCaseTagValid(caseTag)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (caseTagDAO.isNameExist( caseTag.getName() )) {
            return error( En_ResultStatus.ALREADY_EXIST );
        }

        if (!Objects.equals(caseTagDAO.get(caseTag.getId()).getPersonId(), caseTag.getPersonId())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        caseTagDAO.merge(caseTag);

        return ok( caseTag.getId() );
    }

    @Override
    @Transactional
    public Result<Long> removeTag( AuthToken authToken, CaseTag caseTag) {
        if (caseTag.getId() != null && !Objects.equals(caseTagDAO.get(caseTag.getId()).getPersonId(), caseTag.getPersonId())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        return !caseTagDAO.remove(caseTag) ?
                Result.error( En_ResultStatus.NOT_REMOVED) :
                ok(caseTag.getId());
    }

    @Override
    public Result<List<CaseTag>> getTags(AuthToken token, CaseTagQuery query) {
        if ( !policyService.hasGrantAccessFor( token.getRoles(), En_Privilege.ISSUE_VIEW ) ) {
            query.setCompanyId(token.getCompanyId());
        }

        List<CaseTag> caseTags = caseTagDAO.getListByQuery(query);

        return ok(caseTags);
    }

    @Override
    @Transactional
    public Result attachTag(AuthToken authToken, Long caseId, Long tagId) {
        if ( caseId == null || tagId == null ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return lockService.doWithLock(CaseTag.class, caseId, LockStrategy.TRANSACTION, TimeUnit.SECONDS, 5, () -> {
            CaseTag caseTag = caseTagDAO.get(tagId);
            if ( caseTag == null ) {
                return error(En_ResultStatus.NOT_FOUND);
            }
            if (caseObjectTagDAO.checkExists(caseId, tagId)) {
                return error(En_ResultStatus.ALREADY_EXIST);
            }
            CaseObjectTag cot = new CaseObjectTag(caseId, tagId);
            caseObjectTagDAO.persist(cot);

            return ok();
        });
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
}
