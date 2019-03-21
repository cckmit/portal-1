package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dao.ContractDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;

public class ContractServiceImpl implements ContractService {

    @Autowired
    ContractDAO contractDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    PortalConfig portalConfig;
    @Autowired
    PolicyService policyService;
    @Autowired
    AuthService authService;

    @Override
    public CoreResponse<Integer> count(AuthToken token, ContractQuery query) {
        if (!hasGrantAccessFor(token, En_Privilege.CONTRACT_VIEW)) {
            List<Long> managerIds = new ArrayList<>();
            managerIds.add(getCurrentPerson(token).getId());
            query.setManagerIds(managerIds);
        }
        return new CoreResponse<Integer>().success(contractDAO.countByQuery(query));
    }

    @Override
    public CoreResponse<List<Contract>> contractList(AuthToken token, ContractQuery query) {
        if (!hasGrantAccessFor(token, En_Privilege.CONTRACT_VIEW)) {
            List<Long> managerIds = new ArrayList<>();
            managerIds.add(getCurrentPerson(token).getId());
            query.setManagerIds(managerIds);
        }
        List<Contract> list = contractDAO.getListByQuery(query);
        if (list == null) {
            return new CoreResponse<List<Contract>>().error(En_ResultStatus.INTERNAL_ERROR);
        }
        return new CoreResponse<List<Contract>>().success(list);
    }

    @Override
    public CoreResponse<Contract> getContract(AuthToken token, Long id) {

        Contract contract;
        if (hasGrantAccessFor(token, En_Privilege.CONTRACT_VIEW)) {
            contract = contractDAO.get(id);
        } else {
            contract = contractDAO.getByIdAndManagerId(id, getCurrentPerson(token).getId());
        }

        if (contract == null) {
            return new CoreResponse<Contract>().error(En_ResultStatus.NOT_FOUND);
        }

        if (contract.getParentContractId() != null) {
            CaseObject parent = caseObjectDAO.partialGet(contract.getParentContractId(), "CASE_NAME");
            if (parent != null) {
                contract.setParentContractNumber(parent.getName());
            }
        }

        jdbcManyRelationsHelper.fill(contract, "childContracts");
        jdbcManyRelationsHelper.fill(contract, "contractDates");

        return new CoreResponse<Contract>().success(contract);
    }

    @Override
    @Transactional
    public CoreResponse<Long> createContract(AuthToken token, Contract contract) {
        if (!hasGrantAccessFor(token, En_Privilege.CONTRACT_CREATE)) {
            return new CoreResponse<Long>().error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (contract == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INCORRECT_PARAMS);


        CaseObject caseObject = fillCaseObjectFromContract(null, contract);
        Long id = caseObjectDAO.persist(caseObject);
        if (id == null)
            return new CoreResponse<Long>().error(En_ResultStatus.NOT_CREATED);

        contract.setId(id);
        Long contractId = contractDAO.persist(contract);

        if (contractId == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INTERNAL_ERROR);

        jdbcManyRelationsHelper.persist(contract, "contractDates");

        return new CoreResponse<Long>().success(id);
    }

    @Override
    @Transactional
    public CoreResponse<Long> updateContract(AuthToken token, Contract contract) {
        if (!hasGrantAccessFor(token, En_Privilege.CONTRACT_EDIT)) {
            return new CoreResponse<Long>().error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (contract == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = caseObjectDAO.get(contract.getId());
        if ( caseObject == null ) {
            return new CoreResponse<Long>().error(En_ResultStatus.NOT_FOUND);
        }
        fillCaseObjectFromContract(caseObject, contract);
        caseObjectDAO.merge(caseObject);
        contractDAO.merge(contract);
        jdbcManyRelationsHelper.persist(contract, "contractDates");

        return new CoreResponse<Long>().success(contract.getId());
    }

    private CaseObject fillCaseObjectFromContract(CaseObject caseObject, Contract contract) {
        if ( caseObject == null ) {
            caseObject = new CaseObject();
            caseObject.setCaseType(En_CaseType.CONTRACT);
            caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.CONTRACT));
            caseObject.setCreated(new Date());
        } else {
            caseObject.setModified(new Date());
        }

        caseObject.setInfo(contract.getDescription());
        caseObject.setName(contract.getNumber());
        caseObject.setStateId(contract.getState().getId());
        caseObject.setManagerId(contract.getManagerId());
        caseObject.setInitiatorId(contract.getCuratorId());
        caseObject.setInitiatorCompanyId(contract.getContragentId());
        caseObject.setProductId(contract.getDirectionId());

        return caseObject;
    }

    private boolean hasGrantAccessFor(AuthToken token, En_Privilege privilege) {
        UserSessionDescriptor descriptor = authService.findSession(token);
        Set<UserRole> roles = descriptor.getLogin().getRoles();
        return policyService.hasGrantAccessFor(roles, privilege);
    }

    private Person getCurrentPerson(AuthToken token) {
        UserSessionDescriptor descriptor = authService.findSession(token);
        return descriptor.getLogin().getPerson();
    }
}
