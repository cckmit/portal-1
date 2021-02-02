package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.enterprise1c.api.Api1C;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.enterprise1c.dto.Contract1C;
import ru.protei.portal.core.model.enterprise1c.dto.Contractor1C;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.ContractApiQuery;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractorQuery;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.*;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.*;

public class ContractServiceImpl implements ContractService {

    private static final Logger log = LoggerFactory.getLogger(ContractServiceImpl.class);

    @Autowired
    ContractDAO contractDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    ContractorDAO contractorDAO;
    @Autowired
    DevUnitDAO devUnitDAO;
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
    @Autowired
    HistoryService historyService;
    @Autowired
    PortalConfig config;
    @Autowired
    CompanyService companyService;
    @Autowired
    PersonService personService;
    @Autowired
    ProductService productService;

    @Override
    public Result<SearchResult<Contract>> getContracts( AuthToken token, ContractQuery query) {
        if (!hasGrantAccessFor(token, En_Privilege.CONTRACT_VIEW)) {
            query.setManagerIds(singleValueList(token.getPersonId()));
        }
        SearchResult<Contract> sr = contractDAO.getSearchResult(query);
        sr.getResults().forEach(contract -> contract.setProductDirections(new HashSet<>(devUnitDAO.getProjectDirections(contract.getProjectId()))));
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

        contract.setProductDirections(new HashSet<>(devUnitDAO.getProjectDirections(contract.getProjectId())));

        return ok(contract);
    }

    @Override
    @Transactional
    public Result<Long> createContract( AuthToken token, Contract contract) {
        if (!hasGrantAccessFor(token, En_Privilege.CONTRACT_CREATE)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (contract == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (contract.getProjectId() == null) {
            return error(En_ResultStatus.PROJECT_NOT_SELECTED);
        }

        if (contract.getState() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean invalidContractDates = stream(contract.getContractDates())
                .anyMatch(not(this::isValidContractDate));
        if (invalidContractDates) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (contract.getParentContractId() != null) {
            Contract parentContract = contractDAO.get(contract.getParentContractId());
            if (parentContract != null) {
                if (Objects.equals(contract.getOrganizationId(), parentContract.getOrganizationId())) {
                    return error(En_ResultStatus.CONTRACT_ORGANIZATION_SHOULD_BE_DIFFERENT_FROM_PARENT);
                }
            }
        }

        CaseObject caseObject = fillCaseObjectFromContract(null, contract);
        Long contractId = caseObjectDAO.persist(caseObject);
        if (contractId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }
        contract.setId(contractId);

        Contractor contractor = contract.getContractor();
        if (contractor != null) {
            Result<Long> result = saveContractor(contractor);
            if (result.isError()) {
                log.error("createContract(): id = {} | failed to save contractor to db with result = {}", contractId, result);
                throw new RollbackTransactionException(result.getStatus());
            }
            contract.setContractorId(result.getData());
        } else {
            contract.setContractorId(null);
        }

        boolean contractPersisted = contractDAO.persist(contract) != null;
        if (!contractPersisted) {
            log.error("createContract(): id = {} | failed to persist contract to db", contractId);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        Result<Long> historyResult = createStateHistory(token, contractId, En_HistoryAction.ADD, null, contract.getState());
        if (historyResult.isError()) {
            log.error("createContract(): id = {} | failed to create history for contract state : {}", contractId, historyResult.getStatus());
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        try {
            jdbcManyRelationsHelper.persist(contract, "contractDates");
            jdbcManyRelationsHelper.persist(contract, "contractSpecifications");
        } catch (Exception e) {
            log.error("createContract(): id = {} | failed to save contract's relations to db", contractId, e);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED, e);
        }

        boolean contractorDefined = contract.getContractor() != null;
        boolean is1cSync = is1cSyncEnabled(contract) && contractorDefined;
        if (is1cSync) {
            Result<Contract1C> result = saveContract1C(contract);
            if (result.isError()) {
                log.error("createContract(): id = {} | failed to save contract to 1c with result = {}", contractId, result);
                throw new RollbackTransactionException(result.getStatus());
            }
            contract.setRefKey(result.getData().getRefKey());
            boolean contractUpdated = contractDAO.mergeRefKey(contractId, contract.getRefKey());
            if (!contractUpdated) {
                // Not rollback-able error
                log.error("createContract(): id = {} | NO-ROLLBACK | failed to save contract's refKey to db", contractId);
                return error(En_ResultStatus.NOT_CREATED);
            }
        }

        return ok(contractId);
    }

    @Override
    @Transactional
    public Result<Long> updateContract(AuthToken token, Contract contract) {

        Long contractId = contract != null
                ? contract.getId()
                : null;

        if (!hasGrantAccessFor(token, En_Privilege.CONTRACT_EDIT)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (contractId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (contract.getProjectId() == null) {
            return error(En_ResultStatus.PROJECT_NOT_SELECTED);
        }

        if (contract.getState() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean invalidContractDates = stream(contract.getContractDates())
                .anyMatch(not(this::isValidContractDate));
        if (invalidContractDates) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (isNotBlank(contract.getRefKey()) && contract.getContractor() == null) {
            /*
             *  не даем сбрасывать связку договора с договором1С,
             *  она может существовать только, если задан контрагент
             */
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseObject prevContractCaseObject = caseObjectDAO.get(contractId);
        Contract prevContract = contractDAO.get(contractId);
        if (prevContractCaseObject == null || prevContract == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        Contractor contractor = contract.getContractor();
        if (contractor != null) {
            Result<Long> result = saveContractor(contractor);
            if (result.isError()) {
                log.error("updateContract(): id = {} | failed to save contractor to db with result = {}", contractId, result);
                throw new RollbackTransactionException(result.getStatus());
            }
            contract.setContractorId(result.getData());
        } else {
            contract.setContractorId(null);
        }

        fillCaseObjectFromContract(prevContractCaseObject, contract);

        boolean contractCaseObjectSaved = caseObjectDAO.merge(prevContractCaseObject);
        if (!contractCaseObjectSaved) {
            log.error("updateContract(): id = {} | failed to save contract's case object to db", contractId);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        boolean contractSaved = contractDAO.merge(contract);
        if (!contractSaved) {
            log.error("updateContract(): id = {} | failed to save contract to db", contractId);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        boolean contractStateChanged = prevContract.getState() != contract.getState();
        if (contractStateChanged) {
            Result<Long> historyResult = createStateHistory(token, contractId, En_HistoryAction.CHANGE, prevContract.getState(), contract.getState());
            if (historyResult.isError()) {
                log.error("updateContract(): id = {} | failed to create history for changed contract state : {}", contractId, historyResult.getStatus());
                throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
            }
        }

        try {
            jdbcManyRelationsHelper.persist(contract, "contractDates");
            jdbcManyRelationsHelper.persist(contract, "contractSpecifications");
        } catch (Exception e) {
            log.error("updateContract(): id = {} | failed to save contract's relations to db", contractId, e);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED, e);
        }

        boolean is1cSync = is1cSyncEnabled(contract);
        if (is1cSync) {

            boolean isRefKeySet = isNotEmpty(contract.getRefKey());

            boolean isContractorDefined = contract.getContractor() != null;

            boolean isOrganizationRemoved =
                    prevContract.getOrganizationId() != null &&
                    contract.getOrganizationId() == null;

            boolean isOrganizationChanged =
                    prevContract.getOrganizationId() != null &&
                    contract.getOrganizationId() != null &&
                    !Objects.equals(prevContract.getOrganizationId(), contract.getOrganizationId());

            if (isRefKeySet && isOrganizationRemoved) {
                String organizationPrev = prevContract.getOrganizationName();
                Contract1C contract1C = to1C(contract);
                contract1C.setDeletionMark(true);
                Result<Contract1C> removeResult = api1CService.saveContract(contract1C, organizationPrev);
                if (removeResult.isError()) {
                    log.error("updateContract(): id = {} | organization removal | failed to remove contract from 1c with result = {}", contractId, removeResult);
                    throw new RollbackTransactionException(removeResult.getStatus());
                }
                contract.setRefKey(null);
                boolean contractUpdated = contractDAO.mergeRefKey(contract.getId(), contract.getRefKey());
                if (!contractUpdated) {
                    // Not rollback-able error
                    log.error("updateContract(): id = {} | organization removal | NO-ROLLBACK | failed to save contract's refKey to db", contractId);
                    return error(En_ResultStatus.NOT_UPDATED);
                }
            }
            else if (isRefKeySet && isOrganizationChanged) {
                String organizationPrev = prevContract.getOrganizationName();
                String organizationNew = contract.getOrganizationName();
                Contract1C contract1C = to1C(contract);
                contract1C.setDeletionMark(true);
                Result<Contract1C> removeResult = api1CService.saveContract(contract1C, organizationPrev);
                if (removeResult.isError()) {
                    log.error("updateContract(): id = {} | organization modification | failed to remove contract from 1c with result = {}", contractId, removeResult);
                    throw new RollbackTransactionException(removeResult.getStatus());
                }
                contract1C.setRefKey(null);
                contract1C.setDeletionMark(false);
                Result<Contract1C> createResult = api1CService.saveContract(contract1C, organizationNew);
                if (createResult.isError()) {
                    // Not rollback-able error
                    log.error("updateContract(): id = {} | organization modification | NO-ROLLBACK | failed to create contract at 1c with result = {}", contractId, createResult);
                    return error(createResult.getStatus());
                }
                contract.setRefKey(createResult.getData().getRefKey());
                boolean contractUpdated = contractDAO.mergeRefKey(contract.getId(), contract.getRefKey());
                if (!contractUpdated) {
                    // Not rollback-able error
                    log.error("updateContract(): id = {} | organization modification | NO-ROLLBACK | failed to save contract's refKey to db", contractId);
                    return error(En_ResultStatus.NOT_UPDATED);
                }
            }
            else if (isContractorDefined) {
                Result<Contract1C> result = saveContract1C(contract);
                if (result.isError()) {
                    log.error("updateContract(): id = {} | failed to save contractor to 1c with result = {}", contractId, result);
                    throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
                }
                contract.setRefKey(result.getData().getRefKey());
                boolean contractUpdated = contractDAO.mergeRefKey(contract.getId(), contract.getRefKey());
                if (!contractUpdated) {
                    // Not rollback-able error
                    log.error("updateContract(): id = {} | NO-ROLLBACK | failed to save contract's refKey to db", contractId);
                    return error(En_ResultStatus.NOT_UPDATED);
                }
            }
        }

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
    public Result<List<Contractor>> findContractors(AuthToken token, String organization, ContractorQuery query) {
        if (organization == null || query == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValidContractorQuery(query)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Contractor1C queryContractor1C = makeContractor1CFromQuery(query);
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
    @Transactional
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

    @Override
    @Transactional
    public Result<Long> removeContractor(AuthToken token, String organization, String refKey) {

        if (StringUtils.isEmpty(organization) || StringUtils.isEmpty(refKey)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Contractor1C contractor1C = findContractor(organization, queryForRefKey(refKey));
        if (contractor1C == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        Contractor contractor = contractorDAO.getContractorByRefKey(refKey);
        Long contractorId = contractor != null ? contractor.getId() : null;
        boolean savedToDb = contractorId != null;

        if (savedToDb) {
            ContractQuery query = new ContractQuery();
            query.setContractorIds(listOf(contractorId));
            int contractsSize = contractDAO.getSearchResult(query).getTotalCount();
            if (contractsSize > 0) {
                return error(En_ResultStatus.CONTRACTOR_NOT_REMOVED_HAS_CONTRACTS);
            }
        }

        if (savedToDb) {
            boolean removed = contractorDAO.removeByKey(contractorId);
            if (!removed) {
                log.error("removeContractor(): failed to remove contractor from db, but it was removed from 1c integration | refKey = {}", refKey);
                return error(En_ResultStatus.NOT_FOUND);
            }
        }

        contractor1C.setDeletionMark(true);
        Result<Contractor1C> result = api1CService.saveContractor(contractor1C, organization);
        if (result.isError()) {
            log.warn("removeContractor(): failed to save contractor to 1c with refKey = {} | result = {}", refKey, result);
            throw new RollbackTransactionException(En_ResultStatus.NOT_REMOVED);
        }

        return ok(contractorId);
    }

    @Override
    public Result<List<Contract>> getContractsByApiQuery(AuthToken token, ContractApiQuery apiQuery) {
        if (apiQuery == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        List<Contract> contracts = contractDAO.getByApiQuery(apiQuery);
        jdbcManyRelationsHelper.fill(contracts, "contractDates");
        return ok(contracts);
    }

    @Override
    public Result<SelectorsParams> getSelectorsParams(AuthToken token, ContractQuery query) {
        log.debug( "getSelectorsParams(): ContractQuery={} ", query );
        SelectorsParams selectorsParams = new SelectorsParams();

        List<Long> contractorIds = new ArrayList<>(emptyIfNull(query.getContractorIds()));
        if (!CollectionUtils.isEmpty(contractorIds)) {
            List<Contractor> contractors = contractorDAO.getListByKeys(contractorIds);
            if (contractors != null) {
                selectorsParams.setContractors(contractors);
            } else {
                return error(En_ResultStatus.GET_DATA_ERROR, "Error at ContractorIds" );
            }
        }

        List<Long> organisationIds = new ArrayList<>(emptyIfNull(query.getOrganizationIds()));
        if (!CollectionUtils.isEmpty(organisationIds)) {
            Result<List<EntityOption>> result = companyService.companyOptionListByIds(token, organisationIds);
            if (result.isOk()) {
                selectorsParams.setCompanyEntityOptions(result.getData());
            } else {
                return error(result.getStatus(), "Error at OrganisationIds" );
            }
        }

        if ( query.getDirectionId() != null ) {
            Result<List<ProductDirectionInfo>> result = productService.productDirectionList( token, Collections.singletonList(query.getDirectionId()));
            if (result.isOk()) {
                selectorsParams.setProductDirectionInfos(result.getData());
            } else {
                return error(result.getStatus(), "Error at getDirectionId" );
            }
        }

        List<Long> personIds = collectPersonIds(query);
        if (!CollectionUtils.isEmpty( personIds )) {
            Result<List<PersonShortView>> result = personService.shortViewListByIds( personIds );
            if (result.isOk()) {
                selectorsParams.setPersonShortViews(result.getData());
            } else {
                return error(result.getStatus(), "Error at getPersonIds" );
            }
        }

        return ok(selectorsParams);
    }

    private List<Long> collectPersonIds(ContractQuery caseQuery){
        ArrayList<Long> personsIds = new ArrayList<>();
        personsIds.addAll(emptyIfNull(caseQuery.getManagerIds()));
        personsIds.addAll(emptyIfNull(caseQuery.getCuratorIds()));
        return personsIds;
    }

    private Result<Long> createStateHistory(AuthToken token, Long contractId, En_HistoryAction action,
                                            En_ContractState oldState, En_ContractState newState) {
        return historyService.createHistory(
            token,
            contractId,
            action,
            En_HistoryType.CONTRACT_STATE,
            oldState == null ? null : (long) oldState.getId(),
            oldState == null ? null : oldState.name(),
            newState == null ? null : (long) newState.getId(),
            newState == null ? null : newState.name()
        );
    }

    private Contractor1C findContractor(String organization, ContractorQuery query) {
        Contractor1C contractor1C = makeContractor1CFromQuery(query);
        Result<List<Contractor1C>> result = api1CService.getContractors(contractor1C, organization);
        if (result.isError()) {
            return null;
        }
        return getFirst(result.getData());
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

            if (!contractorDAO.merge(contractor)) {
                return error(En_ResultStatus.NOT_UPDATED, "Contractor was not merged");
            }
        }

        return ok(contractor.getId());
    }

    private Result<Contract1C> saveContract1C(Contract contract) {
        if (contract == null || contract.getId() == null
            || contract.getOrganizationId() == null || StringUtils.isBlank(contract.getOrganizationName())) {
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

    private boolean is1cSyncEnabled(Contract contract) {
        boolean sync1cEnabled = config.data().enterprise1C().isContractSyncEnabled();
        boolean stateAgreement = contract.getState() == En_ContractState.AGREEMENT;
        return sync1cEnabled && !stateAgreement;
    }

    private boolean isValidInn(String inn) {
        return innPattern.matcher(inn).matches() && ContractorUtils.checkInn(inn);
    }

    private boolean isValidKpp(String kpp) {
        return kppPattern.matcher(kpp).matches();
    }

    private boolean isValidContractor(Contractor contractor) {
        return isValidInn(contractor.getInn()) &&
               isValidKpp(contractor.getKpp());
    }

    private boolean isValidContract(Contract contract) {
        return contractNumberPattern.matcher(contract.getNumber()).matches()
               && contract.getContractor() != null
               && StringUtils.isNotBlank(contract.getContractor().getRefKey())
               && contract.getDateSigning() != null;
    }

    private boolean isValidContractorQuery(ContractorQuery query) {

        if (query == null) {
            return false;
        }

        if (isNotEmpty(query.getInn()) && !isValidInn(query.getInn())) {
            return false;
        }

        if (isNotEmpty(query.getKpp()) && !isValidKpp(query.getKpp())) {
            return false;
        }

        if (isNotEmpty(query.getKpp()) && isEmpty(query.getInn())) {
            return false;
        }

        return true;
    }

    private boolean isValidContractDate(ContractDate contractDate) {
        boolean isNotifyInvalid = contractDate.isNotify() && contractDate.getDate() == null;
        if (isNotifyInvalid) {
            return false;
        }

        boolean isTypeWithPayment = contractDate.getType() == En_ContractDatesType.PREPAYMENT ||
                contractDate.getType() == En_ContractDatesType.POSTPAYMENT;
        boolean isCostInvalid = (isTypeWithPayment && contractDate.getCost() == null) ||
                (!isTypeWithPayment && contractDate.getCost() != null);
        if (isCostInvalid) {
            return false;
        }

        return true;
    }

    private Contractor1C makeContractor1CFromQuery(ContractorQuery query) {
        Contractor1C contractor1C = new Contractor1C();
        contractor1C.setRefKey(query.getRefKey());
        contractor1C.setInn(query.getInn());
        contractor1C.setKpp(query.getKpp());
        contractor1C.setName(query.getName());
        contractor1C.setFullName(query.getFullName());
        contractor1C.setRegistrationCountryKey(query.getRegistrationCountryKey());
        contractor1C.setDeletionMark(false);
        return contractor1C;
    }

    private ContractorQuery queryForRefKey(String refKey) {
        ContractorQuery query = new ContractorQuery();
        query.setRefKey(refKey);
        return query;
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
        contract1C.setRefKey(StringUtils.isBlank(contract.getRefKey()) ? null : contract.getRefKey().trim());
        contract1C.setNumber(contract.getNumber());
        contract1C.setContractorKey(contract.getContractor().getRefKey());
        contract1C.setDateSigning(saveDateFormat.format(contract.getDateSigning()));
        contract1C.setName(contract.getNumber().trim()+ " от " + showDateFormat.format(contract.getDateSigning()));

        return contract1C;
    }

    public static boolean isSame(Contract1C c1, Contract1C c2) {
        try {
            Date d1 = saveDateFormat.parse(c1.getDateSigning());
            Date d2 = saveDateFormat.parse(c2.getDateSigning());

            if (!Objects.equals(d1,d2)) return false;

        } catch (ParseException e) { return false; }

        return Objects.equals(c1.getNumber(), c2.getNumber())
               && Objects.equals(c1.getContractorKey(), c2.getContractorKey());
    }

    private final Pattern innPattern = Pattern.compile(CONTRACTOR_INN);
    private final Pattern kppPattern = Pattern.compile(CONTRACTOR_KPP);
    private final Pattern contractNumberPattern = Pattern.compile(CONTRACT_NUMBER);

    private final static DateFormat showDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final static DateFormat saveDateFormat = new SimpleDateFormat("yyyy-MM-dd");
}
