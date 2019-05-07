package ru.protei.portal.ui.contract.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.service.ContractService;
import ru.protei.portal.core.service.ContractServiceImpl;
import ru.protei.portal.ui.common.client.service.ContractController;
import ru.protei.portal.ui.common.client.service.ContractController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("ContractController")
public class ContractControllerImpl implements ContractController {

    @Override
    public SearchResult<Contract> getContracts(ContractQuery query) throws RequestFailedException {
        log.debug(" get contracts: offset={} | limit={}", query.getOffset(), query.getLimit());
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(contractService.getSearchResult(token, query));
    }

    @Override
    public Contract getContract(Long id) throws RequestFailedException {
        log.debug(" get contract, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Contract> response = contractService.getContract(descriptor.makeAuthToken(), id);
        log.debug(" get contract, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

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

        log.debug("create contract, id: {}", HelperFunc.nvlt(contract.getId(), "new"));

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        contract.setCreatorId(descriptor.getPerson().getId());

        CoreResponse<Long> response;
        if ( contract.getId() == null ) {
            response = contractService.createContract(descriptor.makeAuthToken(), contract);
        } else {
            response = contractService.updateContract(descriptor.makeAuthToken(), contract);
        }

        log.debug("create contract, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("create contract, applied id: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpRequest);
        log.info("userSessionDescriptor={}", descriptor);
        if (descriptor == null) {
            throw new RequestFailedException(En_ResultStatus.SESSION_NOT_FOUND);
        }

        return descriptor;
    }

    @Autowired
    ContractService contractService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(ContractControllerImpl.class);
}
