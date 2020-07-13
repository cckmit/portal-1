package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;

import java.util.List;

public class ContractQuery extends BaseQuery {

    private Long directionId;

    private En_ContractType type;

    private En_ContractState state;

    private List<Long> managerIds;

    private List<Long> contractorIds;

    private List<Long> organizationIds;

    public Long getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Long directionId) {
        this.directionId = directionId;
    }

    public En_ContractType getType() {
        return type;
    }

    public void setType(En_ContractType type) {
        this.type = type;
    }

    public List<Long> getManagerIds() {
        return managerIds;
    }

    public void setManagerIds(List<Long> managerIds) {
        this.managerIds = managerIds;
    }

    public List<Long> getContractorIds() {
        return contractorIds;
    }

    public void setContractorIds(List<Long> contractorIds) {
        this.contractorIds = contractorIds;
    }

    public En_ContractState getState() {
        return state;
    }

    public void setState(En_ContractState state) {
        this.state = state;
    }

    public List<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }
}
