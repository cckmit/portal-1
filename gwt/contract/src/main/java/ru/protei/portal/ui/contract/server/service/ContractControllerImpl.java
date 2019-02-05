package ru.protei.portal.ui.contract.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.service.ContractService;
import ru.protei.portal.core.service.ContractServiceImpl;
import ru.protei.portal.ui.common.client.service.ContractController;
import ru.protei.portal.ui.common.client.service.ContractController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("ContractController")
public class ContractControllerImpl implements ContractController {
    @Override
    public List<Contract> getContracts(ContractQuery query) throws RequestFailedException {
        log.debug(" get employee registrations: offset={} | limit={}", query.getOffset(), query.getLimit());
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<Contract>> response = contractService.contractList(descriptor.makeAuthToken(), query);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Integer getContractCount(ContractQuery query) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug(" get employee registration count(): query={}", query);
        return contractService.count(descriptor.makeAuthToken(), query).getData();
    }

    @Override
    public Contract getContract(Long id) throws RequestFailedException {
        log.debug(" get employee registration, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Contract> response = contractService.getContract(descriptor.makeAuthToken(), id);
        log.debug(" get employee registration, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Long createContract(Contract contract) throws RequestFailedException {
        if (contract == null) {
            log.warn("null employee registration in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.debug("create employee registration, id: {}", HelperFunc.nvlt(contract.getId(), "new"));

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Long> response = contractService.createContract(descriptor.makeAuthToken(), contract);

        log.debug("create employee registration, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("create employee registration, applied id: {}", response.getData());
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
