package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;
import java.util.Set;

/**
 * Сервис управления компаниями
 */
public interface CompanyService {
    CoreResponse<Long> countCompanies (CompanyQuery query);
    CoreResponse<Long> countGroups (CompanyGroupQuery query);

    CoreResponse<List<EntityOption>> companyOptionList();
    CoreResponse<List<Company>> companyList( CompanyQuery query, Set< UserRole > roles );
    CoreResponse<List<EntityOption>> groupOptionList();
    CoreResponse<List<CompanyGroup>> groupList(CompanyGroupQuery query);
    CoreResponse<List<EntityOption>> categoryOptionList();
    CoreResponse<Company> getCompany( Long id, Set< UserRole > roles );
    CoreResponse<Company> createCompany( Company company, Set< UserRole > roles );
    CoreResponse<Company> updateCompany( Company company, Set< UserRole > roles );
    CoreResponse<Boolean> isCompanyNameExists(String name, Long excludeId);
    CoreResponse<Boolean> isGroupNameExists(String name, Long excludeId);
    /**
     * methods below are for testing purpose only
     */
    CoreResponse<CompanyGroup> createGroup(String name, String info);

//    CoreResponse<CompanyGroupItem> addCompanyToGroup(Long groupId, Long companyId);
//
//    CoreResponse<CompanyGroupItem> delCompanyFromGroup(Long groupId,
//                                                      Long companyId);
}
