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
import ru.protei.portal.core.model.enterprise1c.dto.Contract1C;
import ru.protei.portal.core.model.enterprise1c.dto.Contractor1C;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.*;

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

        if (contract.getContractor() != null) {
            Result<Contract1C> result = saveContract1C(contract);
            if (result.isOk()) {
                contract.setRefKey(result.getData().getRefKey());
            } else {
                return error(En_ResultStatus.INTERNAL_ERROR);
            }

            contractDAO.merge(contract);
        }

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

        /*
           не даем сбрасывать связку с 1С, она может существовать только, если задан контрагент
         */
        if (StringUtils.isNotBlank(contract.getRefKey()) && contract.getContractor() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

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
        } else {
            contract.setContractorId(null);
        }

        if (contract.getContractor() != null) {
            Result<Contract1C> result = saveContract1C(contract);
            if (result.isOk()) {
                contract.setRefKey(result.getData().getRefKey());
            } else {
                return error(En_ResultStatus.INTERNAL_ERROR);
            }
        }

        contractDAO.merge(contract);

        jdbcManyRelationsHelper.persist(contract, "contractDates");
        jdbcManyRelationsHelper.persist(contract, "contractSpecifications");

        return ok(contract.getId());
    }

    @Override
    public Result<List<ContractorCountry>> getContractorCountryList(AuthToken token, String organization) {
        if (organization == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return api1CService.getAllCountries(organization)
                .map(list -> list.stream()
                        .map(country1C -> new ContractorCountry(country1C.getRefKey(), country1C.getName()))
                        .collect(toList()));
    }

    @Override
    public Result<List<Contractor>> getContractorList(AuthToken token) {
        return ok(contractorDAO.getAll());
    }

    @Override
    public Result<List<Contractor>> findContractors(AuthToken token,
                                                    String organization, String contractorInn, String contractorKpp) {
        if (organization == null || contractorInn == null || contractorKpp == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValidContractor(contractorInn, contractorKpp)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Contractor1C queryContractor1C = new Contractor1C();
        queryContractor1C.setInn(contractorInn);
        queryContractor1C.setKpp(contractorKpp);

        Result<List<Contractor1C>> contractors = api1CService.getContractors(queryContractor1C, organization);
        if (contractors.isError()) {
            return error(contractors.getStatus());
        }

        Result<List<ContractorCountry>> contractorCountryListResult = getContractorCountryList(token, organization);
        if (contractorCountryListResult.isError()) {
            return error(contractorCountryListResult.getStatus());
        }

        Map<String, String> mapRefToName = contractorCountryListResult.getData().stream()
                .collect(toMap(ContractorCountry::getRefKey, ContractorCountry::getName, (n1, n2) -> n1));

        return contractors.map(list -> list.stream().map(contractor1C ->
                                from1C(contractor1C, mapRefToName.get(contractor1C.getRegistrationCountryKey())))
                                    .collect(toList())
                );
    }

    @Override
    public Result<Contractor> createContractor(AuthToken token, Contractor contractor) {
        if (contractor == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValidContractor(contractor)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Contractor1C queryContractor1C = to1C(contractor);

        return api1CService.saveContractor(queryContractor1C, contractor.getOrganization())
            .map(contractor1C -> from1C(contractor1C, contractor1C.getRegistrationCountryKey()));
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

    private Result<Contract1C> saveContract1C(Contract contract) {
        if (contract == null || contract.getOrganizationId() == null || StringUtils.isBlank(contract.getOrganizationName())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValidContract(contract)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Contract1C queryContract1C = to1C(contract);

        if (StringUtils.isNotBlank(contract.getRefKey())){
            Result<Contract1C> result = api1CService.getContract(queryContract1C, contract.getOrganizationName());
            if (result.isOk() && result.getData() != null) {
                if (isSame(queryContract1C, result.getData())) {
                    return api1CService.getContract(queryContract1C, contract.getOrganizationName());
                }
            }
        }

        return api1CService.saveContract(queryContract1C, contract.getOrganizationName());
    }

    private boolean hasGrantAccessFor(AuthToken token, En_Privilege privilege) {
        Set<UserRole> roles = token.getRoles();
        return policyService.hasGrantAccessFor(roles, privilege);
    }

    private boolean isValidContractor(String contractorInn, String contractorKpp) {
        return innPattern.matcher(contractorInn).matches() && ContractorUtils.checkInn(contractorInn) &&
                kppPattern.matcher(contractorKpp).matches();
    }

    private boolean isValidContractor(Contractor contractor) {
        return isValidContractor(contractor.getInn(), contractor.getKpp());
    }

    private boolean isValidContract(Contract contract) {
        return contractNumberPattern.matcher(contract.getNumber()).matches()
               && contract.getContractor() != null
               && StringUtils.isNotBlank(contract.getContractor().getRefKey())
               && contract.getDateSigning() != null;
    }

    public static Contractor from1C(Contractor1C contractor1C, String country) {
        Contractor contractor = new Contractor();
        contractor.setRefKey(contractor1C.getRefKey());
        contractor.setName(contractor1C.getName());
        contractor.setFullName(contractor1C.getFullName());
        contractor.setCountry(country);
        contractor.setInn(contractor1C.getInn());
        contractor.setKpp(contractor1C.getKpp());

        return contractor;
    }

    public static Contractor1C to1C(Contractor contractor) {
        Contractor1C contractor1C = new Contractor1C();
        contractor1C.setName(contractor.getName());
        contractor1C.setFullName(contractor.getFullName());
        contractor1C.setInn(contractor.getInn());
        contractor1C.setKpp(contractor.getKpp());
        contractor1C.setRegistrationCountryKey(contractor.getCountryRef());

        return contractor1C;
    }

    public static Contract1C to1C(Contract contract) {
        Contract1C contract1C = new Contract1C();
        contract1C.setRefKey(contract.getRefKey());
        contract1C.setNumber(contract.getNumber());
        contract1C.setContractorKey(contract.getContractor().getRefKey());
        contract1C.setDateSigning(contract.getDateSigning());
        contract1C.setName(contract.getNumber().trim()+ " от " + dateFormat.format(contract.getDateSigning()));

        return contract1C;
    }

    public static boolean isSame(Contract1C c1, Contract1C c2) {
        return Objects.equals(c1.getNumber(), c2.getNumber())
                && Objects.equals(c1.getDateSigning(), c2.getDateSigning())
                && Objects.equals(c1.getContractorKey(), c2.getContractorKey());
    }

    private final Pattern innPattern = Pattern.compile(CONTRACTOR_INN);
    private final Pattern kppPattern = Pattern.compile(CONTRACTOR_KPP);
    private final Pattern contractNumberPattern = Pattern.compile(CONTRACT_NUMBER);

    private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
}
