package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

/**
 * Сервис управления компаниями
 */
public interface CompanyService {

    CoreResponse<List<EntityOption>> companyOptionList();
    CoreResponse<List<Company>> companyList(CompanyQuery query);
    CoreResponse<List<CompanyGroup>> groupList(BaseQuery query);
    CoreResponse<List<CompanyCategory>> categoryList();
    CoreResponse<Company> getCompany(Long id);
    CoreResponse<Company> createCompany(Company company, CompanyGroup group);
    CoreResponse<Company> updateCompany(Company company, CompanyGroup group);
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
