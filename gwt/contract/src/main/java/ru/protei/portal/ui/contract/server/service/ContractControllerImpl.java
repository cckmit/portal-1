package ru.protei.portal.ui.contract.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractorQuery;
import ru.protei.portal.core.service.ContractService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.ContractController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("ContractController")
public class ContractControllerImpl implements ContractController {

    @Override
    public SearchResult<Contract> getContracts(ContractQuery query) throws RequestFailedException {
        log.info(" get contracts: offset={} | limit={}", query.getOffset(), query.getLimit());
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(contractService.getContracts(token, query));
    }

    @Override
    public Contract getContract(Long id) throws RequestFailedException {
        log.info(" get contract, id: {}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<Contract> response = contractService.getContract(token, id);
        log.info(" get contract, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Long saveContract(Contract contract) throws RequestFailedException {
        if (contract == null) {
            log.warn("null contract in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.info("create contract, id: {}", HelperFunc.nvlt(contract.getId(), "new"));

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        contract.setCreatorId(token.getPersonId());

        Result<Long> response;
        if ( contract.getId() == null ) {
            response = contractService.createContract(token, contract);
        } else {
            response = contractService.updateContract(token, contract);
        }

        log.info("create contract, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.info("create contract, applied id: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public List<ContractorCountry> getContractorCountryList(String organization) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(contractService.getContractorCountryList(token, organization));
    }

    @Override
    public List<Contractor> getContractorList() throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(contractService.getContractorList(token));
    }

    @Override
    public List<Contractor> findContractors(String organization, ContractorQuery query) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(contractService.findContractors(token, organization, query));
    }

    @Override
    public Contractor createContractor(Contractor contractor) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(contractService.createContractor(token, contractor));
    }

    @Override
    public Long removeContractor(String organization, String refKey) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(contractService.removeContractor(token, organization, refKey));
    }

    @Override
    public SelectorsParams getSelectorsParams(ContractQuery query) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(contractService.getSelectorsParams( token, query));
    }

    @Override
    public List<CalculationType> getCalculationTypeList(String organization) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(contractService.getCalculationTypeList(token, organization));
    }

    @Autowired
    ContractService contractService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(ContractControllerImpl.class);
}
