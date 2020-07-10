package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Organization;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractorPair;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

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
    public Result<List<String>> getContractorCountryList(AuthToken token) {
        // mock service 1cAPI
        return ok(Arrays.asList("RUSSIA", "USA", "CHINA"));
    }

    @Override
    public Result<List<Contractor>> getContractorList(AuthToken token) {
        return ok(contractorDAO.getAll());
    }

    @Override
    public Result<List<ContractorPair>> findContractors(AuthToken token, En_Organization organization, String contractorINN, String contractorKPP) {
        if (organization == null || contractorINN == null || contractorKPP == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        // mock service 1cAPI
        if ("2311113226".equals(contractorINN) && "111222333".equals(contractorKPP)) {
            Contractor contractor1 = new Contractor();
            contractor1.setName("contractor1");
            contractor1.setRefKey("1C_KEY-ref-1");

            ContractorAPI contractorAPI1 = new ContractorAPI();
            contractorAPI1.setName("contractor1");
            contractorAPI1.setRefKey("1C_KEY-ref-1");
            contractorAPI1.setFullname("fullname contractor1");

            Contractor contractor2 = new Contractor();
            contractor2.setName("contractor2");
            contractor2.setRefKey("1C_KEY-ref-2");

            ContractorAPI contractorAPI2 = new ContractorAPI();
            contractorAPI2.setName("contractor2");
            contractorAPI2.setRefKey("1C_KEY-ref-2");
            contractorAPI2.setFullname("fullname contractor2");
            contractorAPI2.setCountry("RUSSIA");
            contractorAPI2.setInn("2311113226");
            contractorAPI2.setKpp("111222333");

            return ok(Arrays.asList(new ContractorPair(contractor1, contractorAPI1),
                                    new ContractorPair(contractor2, contractorAPI2)));
        } else {
            return ok(new ArrayList<>());
        }
    }

    @Override
    public Result<Contractor> createContractor(AuthToken token, ContractorAPI contractorAPI) {
        if (contractorAPI == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        // mock service 1cAPI
        Contractor contractor = new Contractor();
        contractor.setName(contractorAPI.getName() + " [saved]");
        contractor.setRefKey("1C_KEY-ref");

        return ok(contractor);
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
}
