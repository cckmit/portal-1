package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.enterprise1c.api.Api1C;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.enterprise1c.dto.Contractor1C;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractorPair;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.util.CrmConstants.Company.MAIN_HOME_COMPANY_NAME;
import static ru.protei.portal.core.model.util.CrmConstants.Company.PROTEI_ST_HOME_COMPANY_NAME;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.CONTRACTOR_INN;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.CONTRACTOR_KPP;

public class ContractServiceImpl implements ContractService {

    @Autowired
    ContractDAO contractDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    ContractorDAO contractorDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    PortalConfig portalConfig;
    @Autowired
    PolicyService policyService;
    @Autowired
    AuthService authService;
    @Autowired
    Api1C api1CService;

    @Override
    public Result<SearchResult<Contract>> getContracts( AuthToken token, ContractQuery query) {
        if (!hasGrantAccessFor(token, En_Privilege.CONTRACT_VIEW)) {
            query.setManagerIds(CollectionUtils.singleValueList(token.getPersonId()));
        }
        SearchResult<Contract> sr = contractDAO.getSearchResult(query);
        return ok(sr);
    }

    @Override
    public Result<Contract> getContract( AuthToken token, Long id) {

        Contract contract;
        if (hasGrantAccessFor(token, En_Privilege.CONTRACT_VIEW)) {
            contract = contractDAO.get(id);
        } else {
            contract = contractDAO.getByIdAndManagerId(id, token.getPersonId());
        }

        if (contract == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (contract.getParentContractId() != null) {
            CaseObject parent = caseObjectDAO.partialGet(contract.getParentContractId(), "CASE_NAME");
            if (parent != null) {
                contract.setParentContractNumber(parent.getName());
            }
        }

        jdbcManyRelationsHelper.fill(contract, "childContracts");
        jdbcManyRelationsHelper.fill(contract, "contractDates");
        jdbcManyRelationsHelper.fill(contract, "contractSpecifications");
        Collections.sort(contract.getContractSpecifications());

        return ok(contract);
    }

    @Override
    @Transactional
    public Result<Long> createContract( AuthToken token, Contract contract) {
        if (!hasGrantAccessFor(token, En_Privilege.CONTRACT_CREATE)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (contract == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);


        CaseObject caseObject = fillCaseObjectFromContract(null, contract);
        Long id = caseObjectDAO.persist(caseObject);
        if (id == null)
            return error(En_ResultStatus.NOT_CREATED);

        contract.setId(id);

        Contractor contractor = contract.getContractor();
        if (contractor != null) {
            Result<Long> result = saveContractor(contractor);
            if (result.isOk()) {
                contract.setContractorId(result.getData());
            } else {
                return result;
            }
        }

        Long contractId = contractDAO.persist(contract);

        if (contractId == null)
            return error(En_ResultStatus.INTERNAL_ERROR);

        jdbcManyRelationsHelper.persist(contract, "contractDates");
        jdbcManyRelationsHelper.persist(contract, "contractSpecifications");

        return ok(id);
    }

    @Override
    @Transactional
    public Result<Long> updateContract( AuthToken token, Contract contract) {
        if (!hasGrantAccessFor(token, En_Privilege.CONTRACT_EDIT)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (contract == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = caseObjectDAO.get(contract.getId());
        if (caseObject == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        fillCaseObjectFromContract(caseObject, contract);
        caseObjectDAO.merge(caseObject);

        Contractor contractor = contract.getContractor();
        if (contractor != null) {
            Result<Long> result = saveContractor(contractor);
            if (result.isOk()) {
                contract.setContractorId(result.getData());
            } else {
                return result;
            }
        }

        contractDAO.merge(contract);
        jdbcManyRelationsHelper.persist(contract, "contractDates");
        jdbcManyRelationsHelper.persist(contract, "contractSpecifications");

        return ok(contract.getId());
    }

    @Override
    public Result<List<ContractorCountryAPI>> getContractorCountryList(AuthToken token, String organization) {
        if (organization == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return api1CService.getCountryVocabulary(organization)
                .map(list -> list.stream()
                        .map(country1C -> new ContractorCountryAPI(country1C.getRefKey(), country1C.getName()))
                        .collect(toList()));
    }

    @Override
    public Result<List<Contractor>> getContractorList(AuthToken token) {
        return ok(contractorDAO.getAll());
    }

    @Override
    public Result<List<ContractorPair>> findContractors(AuthToken token,
                                            String organization, String contractorINN, String contractorKPP) {
        if (organization == null || contractorINN == null || contractorKPP == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValidContractor(organization, contractorINN, contractorKPP)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Contractor1C contractor1C = new Contractor1C();
        contractor1C.setInn(contractorINN);
        contractor1C.setKpp(contractorKPP);

        Result<List<ContractorCountryAPI>> contractorCountryListResult = getContractorCountryList(token, organization);
        if (contractorCountryListResult.isError()) {
            return error(contractorCountryListResult.getStatus());
        }

        Map<String, String> mapRefToName = contractorCountryListResult.getData().stream()
                .collect(toMap(ContractorCountryAPI::getRefKey, ContractorCountryAPI::getName, (n1, n2) -> n1));

        return api1CService.getContractors(contractor1C, organization)
                .map(list -> list.stream().map(contractor -> {
                    ContractorAPI contractorAPI = new ContractorAPI();
                    contractorAPI.setRefKey(contractor.getRefKey());
                    contractorAPI.setName(contractor.getName());
                    contractorAPI.setFullName(contractor.getFullName());
                    contractorAPI.setCountry(mapRefToName.get(contractor.getRegistrationCountryKey()));
                    contractorAPI.setInn(contractor.getInn());
                    contractorAPI.setKpp(contractor.getKpp());

                    Contractor contractorDb = new Contractor();
                    contractorDb.setRefKey(contractor.getRefKey());
                    contractorDb.setName(contractor.getName());

                    return new ContractorPair(contractorDb, contractorAPI);
                }).collect(toList()));
    }

    @Override
    public Result<Contractor> createContractor(AuthToken token, ContractorAPI contractorAPI) {
        if (contractorAPI == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValidContractor(contractorAPI)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Contractor1C contractor1C = new Contractor1C();
        contractor1C.setName(contractorAPI.getName());
        contractor1C.setFullName(contractorAPI.getFullName());
        contractor1C.setInn(contractorAPI.getInn());
        contractor1C.setKpp(contractorAPI.getKpp());
        contractor1C.setRegistrationCountryKey(contractorAPI.getCountryRef());

        return api1CService.saveContractor(contractor1C, contractorAPI.getOrganization())
            .map(contractor -> {
                Contractor contractorDb = new Contractor();
                contractorDb.setRefKey(contractor.getRefKey());
                contractorDb.setName(contractor.getName());
                return contractorDb;
            });
    }

    private CaseObject fillCaseObjectFromContract(CaseObject caseObject, Contract contract) {
        if (caseObject == null) {
            caseObject = new CaseObject();
            caseObject.setType(En_CaseType.CONTRACT);
            caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.CONTRACT));
            caseObject.setCreated(new Date());
        } else {
            caseObject.setModified(new Date());
        }

        caseObject.setInfo(contract.getDescription());
        caseObject.setName(contract.getNumber());
        caseObject.setStateId(contract.getState().getId());
        caseObject.setManagerId(contract.getCaseManagerId());
        caseObject.setInitiatorId(contract.getCuratorId());
        caseObject.setInitiatorCompanyId(contract.getCaseContragentId());
        caseObject.setProductId(contract.getCaseDirectionId());

        return caseObject;
    }

    private Result<Long> saveContractor(Contractor contractor) {
        if (contractor.getRefKey() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        Contractor contractorByRefKey = contractorDAO.getContractorByRefKey(contractor.getRefKey());

        if (contractorByRefKey == null) {
            contractorDAO.persist(contractor);
        } else {
            contractor.setId(contractorByRefKey.getId());
            contractorDAO.merge(contractor);
        }

        return ok(contractor.getId());
    }

    private boolean hasGrantAccessFor(AuthToken token, En_Privilege privilege) {
        Set<UserRole> roles = token.getRoles();
        return policyService.hasGrantAccessFor(roles, privilege);
    }

    private boolean isValidContractor(String organization, String contractorINN, String contractorKPP) {
        return (MAIN_HOME_COMPANY_NAME.equals(organization) || PROTEI_ST_HOME_COMPANY_NAME.equals(organization)) &&
                innPattern.matcher(contractorINN).matches() && ContractorUtils.checkInn(contractorINN) &&
                kppPattern.matcher(contractorKPP).matches();
    }

    private boolean isValidContractor(ContractorAPI contractorAPI) {
        return isValidContractor(contractorAPI.getOrganization(), contractorAPI.getInn(), contractorAPI.getKpp());
    }

    private final Pattern innPattern = Pattern.compile(CONTRACTOR_INN);
    private final Pattern kppPattern = Pattern.compile(CONTRACTOR_KPP);
}
