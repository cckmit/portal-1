package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Сервис управления компаниями
 */
public interface CompanyService {

    @Privileged({ En_Privilege.COMPANY_VIEW })
    Result<SearchResult<Company>> getCompanies(AuthToken token, CompanyQuery query);

    Result<List<EntityOption>> companyOptionList(AuthToken token, CompanyQuery query);

    Result<List<EntityOption>> companyOptionListIgnorePrivileges(AuthToken token, CompanyQuery query);

    Result<List<EntityOption>> companyOptionListByIds(AuthToken token, List<Long> ids);

    Result<List<EntityOption>> subcontractorOptionListByCompanyIds(AuthToken token, Collection<Long> companyIds, boolean isActive);

    Result<List<EntityOption>> companyOptionListBySubcontractorIds(AuthToken token, Collection<Long> subcontractorIds, boolean isActive);

    @Privileged( En_Privilege.COMPANY_EDIT )
    @Auditable( En_AuditType.COMPANY_MODIFY )
    Result<?> updateState(AuthToken makeAuthToken, Long companyId, boolean isDeprecated);

    Result<List<EntityOption>> groupOptionList(AuthToken token);

    Result<List<CompanyGroup>> groupList(AuthToken token, CompanyGroupQuery query);

    Result<List<En_CompanyCategory>> categoryOptionList(AuthToken token, boolean hasOfficial);

    @Privileged( En_Privilege.COMPANY_VIEW )
    Result<Company> getCompany( AuthToken token, Long id );

    Result<Company> getCompanyOmitPrivileges(AuthToken token, Long id );

    @Privileged( En_Privilege.COMPANY_CREATE )
    @Auditable( En_AuditType.COMPANY_CREATE )
    Result<Company> createCompany( AuthToken token, Company company );

    @Privileged( En_Privilege.COMPANY_EDIT )
    @Auditable( En_AuditType.COMPANY_MODIFY )
    Result<Company> updateCompany( AuthToken token, Company company );

    Result<Boolean> isCompanyNameExists(AuthToken token, String name, Long excludeId);

    Result<Boolean> isGroupNameExists(AuthToken token, String name, Long excludeId);

    Result<List<CompanySubscription>> getCompanySubscriptions(AuthToken token, Long companyId );

    Result<List<CompanySubscription>> getCompanyWithParentCompanySubscriptions( AuthToken authToken, Set<Long> companyIds );

    Result<List<Company>> getAllHomeCompanies(AuthToken token);

    Result<List<CompanyImportanceItem>> getCompanyImportanceItems(AuthToken token, Long companyId);
}
