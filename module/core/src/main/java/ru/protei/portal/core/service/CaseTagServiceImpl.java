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
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObjectTag;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.service.user.AuthService;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return templateGetTagsByPermission(token,
                query -> {
                    List<Long> caseIds = caseObjectTagDAO.getListByCaseId(caseId).stream()
                            .map(CaseObjectTag::getTagId).collect(Collectors.toList());
                    query.setIds(caseIds);
                    return query;
                });
    }

    @Override
    public CoreResponse<List<CaseTag>> getTagsByCaseType(AuthToken token, En_CaseType caseType) {
        return templateGetTagsByPermission(token,
                query -> {
                    query.setCaseTypes(Collections.singletonList(caseType));
                    return query;
                });
    }

    private CoreResponse<List<CaseTag>> templateGetTagsByPermission(AuthToken token, Function<CaseTagQuery, CaseTagQuery> specify){
        CaseTagQuery query = specify.apply(new CaseTagQuery());
        boolean showCompanyNameInView = checkPermissionAndSetCompanyInQuery(token, query);

        List<CaseTag> caseTags = caseTagDAO.getListByQuery(query);
        setShowCompanyNameInView(showCompanyNameInView, caseTags);

        return new CoreResponse<List<CaseTag>>().success(caseTags);
    }

    private boolean checkPermissionAndSetCompanyInQuery(AuthToken token, CaseTagQuery query) {
        boolean showCompanyNameInView;
        if (!policyService.hasScopeForPrivilege(getRoles(token), En_Privilege.ISSUE_VIEW, En_Scope.SYSTEM)) {
            Long companyId = authService.findSession( token ).getCompany().getId();
            query.setCompanyId(companyId);
            showCompanyNameInView = false;
        } else {
            showCompanyNameInView = true;
        }
        return showCompanyNameInView;
    }

    private void setShowCompanyNameInView(boolean showCompanyNameInView, Collection<CaseTag> caseTags) {
        caseTags.forEach(tag -> tag.setShowCompanyInView(showCompanyNameInView));
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
        if (caseTag.getCompany() == null){
            return false;
        }
        return true;
    }

    private Set<UserRole> getRoles(AuthToken token) {
        return Optional.ofNullable(authService.findSession(token))
                .map(d -> d.getLogin().getRoles())
                .orElse(null);
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
