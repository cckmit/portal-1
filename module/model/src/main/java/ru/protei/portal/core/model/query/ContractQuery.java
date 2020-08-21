package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;

import java.util.List;

public class ContractQuery extends BaseQuery {

    private Long directionId;

    private List<En_ContractType> types;

    private List<En_ContractState> states;

    private List<Long> managerIds;

    private List<Long> contractorIds;

    private List<Long> organizationIds;

    private List<Long> parentContractIds;

    public Long getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Long directionId) {
        this.directionId = directionId;
    }

    public List<En_ContractType> getTypes() {
        return types;
    }

    public void setTypes(List<En_ContractType> types) {
        this.types = types;
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

    public List<En_ContractState> getStates() {
        return states;
    }

    public void setStates(List<En_ContractState> states) {
        this.states = states;
    }

    public List<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public List<Long> getParentContractIds() {
        return parentContractIds;
    }

    public void setParentContractIds(List<Long> parentContractIds) {
        this.parentContractIds = parentContractIds;
    }

    @Override
    public String toString() {
        return "ContractQuery{" +
                "directionId=" + directionId +
                ", types=" + types +
                ", states=" + states +
                ", managerIds=" + managerIds +
                ", contractorIds=" + contractorIds +
                ", organizationIds=" + organizationIds +
                ", parentContractIds=" + parentContractIds +
                '}';
    }
}
