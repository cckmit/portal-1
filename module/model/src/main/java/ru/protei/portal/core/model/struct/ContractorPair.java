package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.ent.ContractorAPI;

import java.io.Serializable;

public class ContractorPair implements Serializable {
    private Contractor contractor;
    private ContractorAPI contractorAPI;

    public ContractorPair() {
    }

    public ContractorPair(Contractor contractor, ContractorAPI contractorAPI) {
        this.contractor = contractor;
        this.contractorAPI = contractorAPI;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public ContractorAPI getContractorAPI() {
        return contractorAPI;
    }
}
