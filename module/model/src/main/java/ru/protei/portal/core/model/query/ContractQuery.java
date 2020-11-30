package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_ContractKind;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.struct.DateRange;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;

public class ContractQuery extends BaseQuery {

    private Long directionId;

    private DateRange dateSigningRange;

    private DateRange dateValidRange;

    private En_ContractKind kind;

    private List<En_ContractType> types;

    private List<Long> caseTagsIds;

    private List<En_ContractState> states;

    private List<Long> managerIds;

    private List<Long> contractorIds;

    private List<Long> organizationIds;

    private List<Long> parentContractIds;

    private List<Long> curatorIds;

    public Long getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Long directionId) {
        this.directionId = directionId;
    }

    public DateRange getDateSigningRange() {
        return dateSigningRange;
    }

    public void setDateSigningRange(DateRange dateSigningRange) {
        this.dateSigningRange = dateSigningRange;
    }

    public DateRange getDateValidRange() {
        return dateValidRange;
    }

    public void setDateValidRange(DateRange dateValidRange) {
        this.dateValidRange = dateValidRange;
    }

    public En_ContractKind getKind() {
        return kind;
    }

    public void setKind(En_ContractKind kind) {
        this.kind = kind;
    }

    public List<En_ContractType> getTypes() {
        return types;
    }

    public void setTypes(List<En_ContractType> types) {
        this.types = types;
    }

    public List<Long> getCaseTagsIds() {
        return caseTagsIds;
    }

    public void setCaseTagsIds(List<Long> caseTagsIds) {
        this.caseTagsIds = caseTagsIds;
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

    public List<Long> getCuratorIds() {
        return curatorIds;
    }

    public void setCuratorIds(List<Long> curatorIds) {
        this.curatorIds = curatorIds;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                directionId != null ||
                dateSigningRange != null ||
                dateValidRange != null ||
                kind != null ||
                isNotEmpty(types) ||
                isNotEmpty(states) ||
                isNotEmpty(caseTagsIds) ||
                isNotEmpty(managerIds) ||
                isNotEmpty(contractorIds) ||
                isNotEmpty(curatorIds) ||
                isNotEmpty(organizationIds) ||
                isNotEmpty(parentContractIds);
    }

    @Override
    public String toString() {
        return "ContractQuery{" +
                "directionId=" + directionId +
                ", dateSigningRange=" + dateSigningRange +
                ", dateValidRange=" + dateValidRange +
                ", kind=" + kind +
                ", types=" + types +
                ", states=" + states +
                ", caseTagsIds=" + caseTagsIds +
                ", managerIds=" + managerIds +
                ", contractorIds=" + contractorIds +
                ", curatorIds=" + curatorIds +
                ", organizationIds=" + organizationIds +
                ", parentContractIds=" + parentContractIds +
                '}';
    }
}
