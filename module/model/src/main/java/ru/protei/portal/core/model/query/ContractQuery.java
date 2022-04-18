package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_ContractKind;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.DateRange;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;

public class ContractQuery extends BaseQuery {

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

    private List<String> refKeys;

    private Date openStateDate;

    private String deliveryNumber;

    private Long projectId;

    public List<Long> directionIds;

    public List<Long> getDirectionIds() {
        return directionIds;
    }

    public void setDirectionIds(List<Long> directionIds) {
        this.directionIds = directionIds;
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

    public List<String> getRefKeys() {
        return refKeys;
    }

    public void setRefKeys(List<String> refKeys) {
        this.refKeys = refKeys;
    }

    public Date getOpenStateDate() {
        return openStateDate;
    }

    public void setOpenStateDate(Date openStateDate) {
        this.openStateDate = openStateDate;
    }

    public String getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                dateSigningRange != null ||
                dateValidRange != null ||
                kind != null ||
                openStateDate != null ||
                StringUtils.isNotEmpty(deliveryNumber) ||
                projectId != null ||
                isNotEmpty(types) ||
                isNotEmpty(states) ||
                isNotEmpty(caseTagsIds) ||
                isNotEmpty(managerIds) ||
                isNotEmpty(contractorIds) ||
                isNotEmpty(directionIds) ||
                isNotEmpty(curatorIds) ||
                isNotEmpty(organizationIds) ||
                isNotEmpty(parentContractIds) ||
                isNotEmpty(refKeys);
    }

    @Override
    public String toString() {
        return "ContractQuery{" +
                "directionIds=" + directionIds +
                ", dateSigningRange=" + dateSigningRange +
                ", dateValidRange=" + dateValidRange +
                ", kind=" + kind +
                ", types=" + types +
                ", caseTagsIds=" + caseTagsIds +
                ", states=" + states +
                ", managerIds=" + managerIds +
                ", contractorIds=" + contractorIds +
                ", organizationIds=" + organizationIds +
                ", parentContractIds=" + parentContractIds +
                ", curatorIds=" + curatorIds +
                ", refKeys=" + refKeys +
                ", openStateDate=" + openStateDate +
                ", deliveryNumber=" + deliveryNumber +
                ", projectId=" + projectId +
                '}';
    }
}
