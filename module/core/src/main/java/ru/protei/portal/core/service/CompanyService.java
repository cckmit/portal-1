package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
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

import java.util.List;

/**
 * Сервис управления компаниями
 */
public interface CompanyService {

    @Privileged({ En_Privilege.COMPANY_VIEW })
    CoreResponse<Long> countCompanies (AuthToken token, CompanyQuery query);

    CoreResponse<Long> countGroups (CompanyGroupQuery query);

    CoreResponse<List<EntityOption>> companyOptionList(List<En_CompanyCategory> categories);

    @Privileged( En_Privilege.COMPANY_VIEW )
    CoreResponse<List<Company>> companyList(AuthToken token, CompanyQuery query);
    CoreResponse<List<EntityOption>> groupOptionList();
    CoreResponse<List<CompanyGroup>> groupList(CompanyGroupQuery query);
    CoreResponse<List<EntityOption>> categoryOptionList(boolean hasOfficial);

    @Privileged( En_Privilege.COMPANY_VIEW )
    CoreResponse<Company> getCompany(AuthToken token, Long id );

    @Privileged( En_Privilege.COMPANY_CREATE )
    @Auditable( En_AuditType.COMPANY_CREATE )
    CoreResponse<Company> createCompany( AuthToken token, Company company );

    @Privileged( En_Privilege.COMPANY_EDIT )
    @Auditable( En_AuditType.COMPANY_MODIFY )
    CoreResponse<Company> updateCompany( AuthToken token, Company company );

    CoreResponse<Boolean> isCompanyNameExists(String name, Long excludeId);
    CoreResponse<Boolean> isGroupNameExists(String name, Long excludeId);

    CoreResponse<Boolean> updateCompanySubscriptions( Long id, List<CompanySubscription> value );
    CoreResponse<List<CompanySubscription>> getCompanySubscriptions( Long companyId );

    /**
     * methods below are for testing purpose only
     */
    CoreResponse<CompanyGroup> createGroup(String name, String info);

}
