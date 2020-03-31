package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления компаниями
 */
public interface CompanyService {

    @Privileged({ En_Privilege.COMPANY_VIEW })
    Result<SearchResult<Company>> getCompanies( AuthToken token, CompanyQuery query);
    Result<List<EntityOption>> companyOptionList( AuthToken token, CompanyQuery query);
    Result<List<EntityOption>> companyOptionListByIds( List<Long> ids);

    @Privileged( En_Privilege.COMPANY_EDIT )
    @Auditable( En_AuditType.COMPANY_MODIFY )
    Result<?> updateState( AuthToken makeAuthToken, Long companyId, boolean isDeprecated);

    Result<List<EntityOption>> groupOptionList();
    Result<List<CompanyGroup>> groupList( CompanyGroupQuery query);
    Result<List<En_CompanyCategory>> categoryOptionList( boolean hasOfficial);

    @Privileged( En_Privilege.COMPANY_VIEW )
    Result<Company> getCompany( AuthToken token, Long id );
    Result<Company> getCompanyUnsafe( AuthToken token, Long id );

    @Privileged( En_Privilege.COMPANY_CREATE )
    @Auditable( En_AuditType.COMPANY_CREATE )
    Result<Company> createCompany( AuthToken token, Company company );

    @Privileged( En_Privilege.COMPANY_EDIT )
    @Auditable( En_AuditType.COMPANY_MODIFY )
    Result<Company> updateCompany( AuthToken token, Company company );

    Result<Boolean> isCompanyNameExists( String name, Long excludeId);
    Result<Boolean> isGroupNameExists( String name, Long excludeId);

    Result<List<CompanySubscription>> getCompanySubscriptions( Long companyId );
    Result<List<CompanySubscription>> getCompanyWithParentCompanySubscriptions( AuthToken authToken, Long companyId );

    Result<List<Long>> getAllHomeCompanyIds(AuthToken token);
}
