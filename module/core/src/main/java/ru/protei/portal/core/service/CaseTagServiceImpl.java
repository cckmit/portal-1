package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.CaseObjectTagDAO;
import ru.protei.portal.core.model.dao.CaseTagDAO;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObjectTag;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.LockStrategy;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;
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
    @Autowired
    HistoryService historyService;

    @Override
    @Transactional
    public Result<Long> create( AuthToken authToken, CaseTag caseTag) {
        caseTag.setPersonId( authToken.getPersonId() );

        if (caseTag.getId() != null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isCaseTagValid(caseTag)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (caseTagDAO.isNameUniqueForTag( null, caseTag.getCaseType(), caseTag.getCompanyId(), caseTag.getName() )) {
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

        if (caseTagDAO.isNameUniqueForTag( caseTag.getId(), caseTag.getCaseType(), caseTag.getCompanyId(), caseTag.getName() )) {
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
    public Result<Long> removeTag( AuthToken authToken, Long caseTagId) {
        if (caseTagId != null && !Objects.equals(caseTagDAO.get(caseTagId).getPersonId(), authToken.getPersonId())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        return !caseTagDAO.removeByKey(caseTagId) ?
                Result.error(En_ResultStatus.NOT_REMOVED) :
                ok(caseTagId);
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
    public Result<List<CaseObjectTag>> getCaseObjectTags(AuthToken token, List<Long> caseIds) {
        List<Long> companyIds = null;
        if (!policyService.hasGrantAccessFor(token.getRoles(), En_Privilege.ISSUE_VIEW)) {
            companyIds = singletonList(token.getCompanyId());
        }
        List<CaseObjectTag> tags = caseObjectTagDAO.list(caseIds, null, companyIds);
        return ok(tags);
    }

    @Override
    public Result<CaseTag> getTag(AuthToken token, Long tagId) {
        if (tagId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseTagQuery query = new CaseTagQuery();
        query.setIds(singletonList(tagId));

        Result<List<CaseTag>> tags = getTags(token, query);

        if (CollectionUtils.isEmpty(tags.getData())) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(tags.getData().iterator().next());
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

            if (createHistory(authToken, caseId, En_HistoryAction.ADD, null, caseTag).isError()) {
                throw createHistoryException(En_ResultStatus.NOT_CREATED, En_HistoryAction.ADD, tagId);
            }

            return ok();
        });
    }

    @Override
    @Transactional
    public Result<Long> detachTag( AuthToken authToken, Long caseId, Long tagId) {
        if ( caseId == null || tagId == null ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseTag caseTag = caseTagDAO.get(tagId);
        if ( caseTag == null ) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        caseObjectTagDAO.removeByCaseIdAndTagId(caseId, tagId);

        if (createHistory(authToken, caseId, En_HistoryAction.REMOVE, caseTag, null).isError()) {
            throw createHistoryException(En_ResultStatus.NOT_REMOVED, En_HistoryAction.REMOVE, tagId);
        }

        return ok(tagId);
    }

    @Override
    @Transactional
    public void addItemsToHistory(AuthToken authToken, Long caseId, List<CaseTag> tags) {
        tags.forEach(tag -> {
            if (createHistory(authToken, caseId, En_HistoryAction.ADD, null, tag).isError()) {
                throw createHistoryException(En_ResultStatus.NOT_CREATED, En_HistoryAction.ADD, tag.getId());
            }
        });
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

    private Result<Long> createHistory(AuthToken token, Long id, En_HistoryAction action, CaseTag oldCaseTag, CaseTag newCaseTag) {
        return historyService.createHistory(token, id, action,
                En_HistoryType.TAG,
                oldCaseTag == null ? null : oldCaseTag.getId(),
                oldCaseTag == null ? null : oldCaseTag.getName(),
                newCaseTag == null ? null : newCaseTag.getId(),
                newCaseTag == null ? null : newCaseTag.getName()
        );
    }

    private ResultStatusException createHistoryException(En_ResultStatus status, En_HistoryAction action, Long caseTagId) {
        return new ResultStatusException(
                status,
                String.format("Failed to create history for case tag. action=%s, caseTagId=%d", action, caseTagId)
        );
    }
}
