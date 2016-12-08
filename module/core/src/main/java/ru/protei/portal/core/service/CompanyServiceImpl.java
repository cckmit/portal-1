package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CompanyCategoryDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CompanyGroupQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.EntityOption;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления компаниями
 */
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    CompanyGroupDAO companyGroupDAO;


    @Autowired
    CompanyCategoryDAO companyCategoryDAO;
    /**
     * caches
     */


    /**
     * it will be called after auto-injection
     */
    @PostConstruct
    protected void init() {
    }

    /**
     * destructor implementation
     */
    @PreDestroy
    protected void destroy () {
    }


    @Override
    public CoreResponse<Long> countCompanies(CompanyQuery query) {
        return new CoreResponse<Long>().success(companyDAO.count(query));
    }

    @Override
    public CoreResponse<Long> countGroups(CompanyGroupQuery query) {
        return new CoreResponse<Long>().success(companyGroupDAO.count(query));
    }


    @Override
    public CoreResponse<List<EntityOption>> companyOptionList() {
        List<Company> list = companyDAO.getListByQuery(new CompanyQuery("", En_SortField.comp_name, En_SortDir.ASC));

        if (list == null)
            new CoreResponse<List<EntityOption>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream().map(Company::toEntityOption).collect(Collectors.toList());

        return new CoreResponse<List<EntityOption>>().success(result,result.size());
    }

    @Override
    public CoreResponse<CompanyGroup> createGroup(String name, String info) {

        CompanyGroup group = new CompanyGroup();
        group.setCreated(new Date());
        group.setInfo(info);
        group.setName(name);

        if (companyGroupDAO.persist(group) != null) {
            return new CoreResponse<CompanyGroup>().success(group);
        }

        return createUndefinedError();
    }

    private <T> CoreResponse<T> createUndefinedError() {
        return new CoreResponse<T>().error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public CoreResponse<List<Company>> companyList(CompanyQuery query) {

        List<Company> list = companyDAO.getListByQuery(query);

        if (list == null)
            new CoreResponse<List<Company>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<Company>>().success(list);
    }

    @Override
    public CoreResponse<List<EntityOption>> groupOptionList() {
        List<CompanyGroup> list = companyGroupDAO.getListByQuery(new CompanyGroupQuery(null, En_SortField.group_name, En_SortDir.ASC));

        if (list == null)
            new CoreResponse<List<EntityOption>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream().map(CompanyGroup::toEntityOption).collect(Collectors.toList());

        return new CoreResponse<List<EntityOption>>().success(result,result.size());
    }

    @Override
    public CoreResponse<List<CompanyGroup>> groupList(CompanyGroupQuery query) {
        return new CoreResponse<List<CompanyGroup>>().success(
                companyGroupDAO.getListByQuery(query)
        );
    }

    @Override
    public CoreResponse<List<EntityOption>> categoryOptionList() {
        List<CompanyCategory> list = companyCategoryDAO.getAll();

        if (list == null)
            new CoreResponse<List<EntityOption>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream().map(CompanyCategory::toEntityOption).collect(Collectors.toList());

        return new CoreResponse<List<EntityOption>>().success(result,result.size());
    }

    @Override
    public CoreResponse<Company> getCompany( Long id ) {

        if (id == null) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Company company = companyDAO.get(id);

        if (company == null) {
            return new CoreResponse().error(En_ResultStatus.NOT_FOUND);
        }

        return new CoreResponse<Company>().success(company);
    }

    @Override
    public CoreResponse<Company> createCompany(Company company) {

        if (!isValidCompany(company)) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        company.setCreated(new Date());
        Long companyId = companyDAO.persist(company);

        if (companyId == null) {
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);
        }

        return new CoreResponse<Company>().success(company);
    }

    @Override
    public CoreResponse<Company> updateCompany(Company company) {

        if (!isValidCompany(company)) {
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Boolean result = companyDAO.merge(company);

        if ( !result )
            new CoreResponse().error(En_ResultStatus.NOT_UPDATED);

        return new CoreResponse<Company>().success(company);
    }

    @Override
    public CoreResponse<Boolean> isCompanyNameExists(String name, Long excludeId) {

        if (name == null || name.trim().isEmpty())
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        return new CoreResponse<Boolean>().success(checkCompanyExists(name, excludeId));
    }

    @Override
    public CoreResponse<Boolean> isGroupNameExists(String name, Long excludeId) {

        if (name == null || name.trim().isEmpty())
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        return new CoreResponse<Boolean>().success(checkGroupExists(name, excludeId));
    }

    private boolean isValidCompany(Company company) {
        return company != null
                && company.getCname() != null
                && !company.getCname().trim().isEmpty()
                && isValidContactInfo(company)
                && !checkCompanyExists(company.getCname(), company.getId());
    }

    private boolean isValidContactInfo (Company company) {
        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(company.getContactInfo());

        return HelperFunc.isNotEmpty(infoFacade.getLegalAddress()) &&
                HelperFunc.isNotEmpty(infoFacade.getFactAddress());
    }

    private boolean isValidGroup(CompanyGroup group) {
        return group != null &&
                group.getName() != null && !group.getName().trim().isEmpty() &&
                !checkGroupExists(group.getName(), group.getId());
    }



    private boolean checkCompanyExists (String name, Long excludeId) {

        Company company = companyDAO.getCompanyByName(name);

        if (company == null)
            return false;

        if (excludeId != null && company.getId().equals(excludeId))
            return false;

        return true;
    }

    private boolean checkGroupExists (String name, Long excludeId) {

        CompanyGroup group = companyGroupDAO.getGroupByName(name);

        if (group == null)
            return false;

        if (excludeId != null && group.getId().equals(excludeId))
            return false;

        return true;
    }
}