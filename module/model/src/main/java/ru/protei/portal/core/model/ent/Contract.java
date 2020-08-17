package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.converter.MoneyJdbcConverter;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EntityOptionSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Договор
 */
@JdbcEntity(table = "contract")
public class Contract extends AuditableObject implements Serializable, EntityOptionSupport {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    @JdbcColumn(name = "ref_key")
    private String refKey;

    /**
     * Создатель договора
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = "CREATOR", table = "case_object", sqlTableAlias = "CO")
    private Long creatorId;

    /**
     * Дата создания
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "CREATED", sqlTableAlias = "CO")
    private Date created;

    /**
     * Дата изменения
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "MODIFIED", sqlTableAlias = "CO")
    private Date modified;

    /**
     * Менеджер
     */
    @JdbcJoinedColumn(localColumn = "project_id", table = "case_object", remoteColumn = "id", mappedColumn = "MANAGER", sqlTableAlias = "P")
    private Long managerId;

    @JdbcJoinedColumn(mappedColumn = "displayShortName", joinPath = {
            @JdbcJoinPath(localColumn = "project_id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "MANAGER", remoteColumn = "id", table = "person")
    })
    private String managerShortName;

    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "MANAGER", sqlTableAlias = "CO")
    private Long caseManagerId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "MANAGER", remoteColumn = "id", table = "person")
    }, mappedColumn = "displayShortName")
    private String caseManagerShortName;

    /**
     * Куратор договора
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "INITIATOR", sqlTableAlias = "CO")
    private Long curatorId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "INITIATOR", remoteColumn = "id", table = "person")
    }, mappedColumn = "displayShortName")
    private String curatorShortName;

    /**
     * Направление
     */
    @JdbcJoinedColumn(localColumn = "project_id", table = "case_object", remoteColumn = "id", mappedColumn = "product_id", sqlTableAlias = "P")
    private Long directionId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "project_id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "product_id", remoteColumn = "id", table = "dev_unit")
    }, mappedColumn = "UNIT_NAME")
    private String directionName;

    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "product_id", sqlTableAlias = "CO")
    private Long caseDirectionId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "product_id", remoteColumn = "id", table = "dev_unit")
    }, mappedColumn = "UNIT_NAME")
    private String caseDirectionName;

    /**
     * Текущее состояние договора
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "STATE", sqlTableAlias = "CO")
    private Integer stateId;

    /**
     * Предмет договора
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "INFO", sqlTableAlias = "CO")
    private String description;

    /**
     * Номер
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "CASE_NAME", sqlTableAlias = "CO")
    private String number;

    /**
     * Сумма
     */
    @JdbcColumn(name = "cost", converterType = ConverterType.CUSTOM, converter = MoneyJdbcConverter.class)
    private Money cost;

    /**
     * Валюта
     */
    @JdbcColumn(name = "cost_currency")
    @JdbcEnumerated(EnumType.ID)
    private En_Currency currency;

    /**
     * НДС
     */
    @JdbcColumn(name = "cost_vat")
    private Long vat;

    /**
     * Типы
     */
    @JdbcEnumerated(EnumType.ID)
    @JdbcColumnCollection(name = "contract_types", separator = ",")
    private List<En_ContractType> contractTypes;

    @JdbcColumn(name = "date_signing")
    private Date dateSigning;

    @JdbcColumn(name = "date_valid")
    private Date dateValid;

    @JdbcOneToMany(localColumn = "id", remoteColumn = "contract_id")
    private List<ContractDate> contractDates;

    @JdbcOneToMany(localColumn = "id", remoteColumn = "contract_id")
    private List<ContractSpecification> contractSpecifications;

    @JdbcColumn(name = "organization_id")
    private Long organizationId;

    @JdbcJoinedColumn(localColumn = "organization_id", table = "company", remoteColumn = "id", mappedColumn = "cname")
    private String organizationName;

    @JdbcColumn(name = "parent_contract_id")
    private Long parentContractId;

    // winter не поддерживает JdbcJoinedObject на ту же сущность во избежание рекурсии
    private String parentContractNumber;

    @JdbcOneToMany(table = "contract", localColumn = "id", remoteColumn = "parent_contract_id")
    private List<Contract> childContracts;

    @JdbcColumn(name = "project_id")
    private Long projectId;

    @JdbcJoinedColumn(localColumn = "project_id", table = "case_object", remoteColumn = "id", mappedColumn = "CASE_NAME", sqlTableAlias = "case_object")
    private String projectName;

    @JdbcJoinedColumn(localColumn = "project_id", table = "case_object", remoteColumn = "id", mappedColumn = Project.Columns.CUSTOMER_TYPE, sqlTableAlias = "case_object")
    @JdbcEnumerated(EnumType.ID)
    private En_CustomerType projectCustomerType;

    @JdbcColumn(name = "contractor_id")
    private Long contractorId;

    @JdbcJoinedObject( localColumn = "contractor_id", remoteColumn = "id", sqlTableAlias = "C")
    private Contractor contractor;


    @Override
    public String getAuditType() {
        return "Contract";
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public En_ContractState getState () {
        return En_ContractState.getById(this.stateId);
    }

    public void setState (En_ContractState state) {
        this.stateId = state.getId();
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getCuratorId() {
        return curatorId;
    }

    public void setCuratorId(Long curatorId) {
        this.curatorId = curatorId;
    }

    public Long getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Long directionId) {
        this.directionId = directionId;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Money getCost() {
        return cost;
    }

    public void setCost(Money cost) {
        this.cost = cost;
    }

    public En_Currency getCurrency() {
        return currency;
    }

    public void setCurrency(En_Currency currency) {
        this.currency = currency;
    }

    public Long getVat() {
        return vat;
    }

    public void setVat(Long vat) {
        this.vat = vat;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<En_ContractType> getContractTypes() {
        return contractTypes;
    }

    public void setContractTypes(List<En_ContractType> contractTypes) {
        this.contractTypes = contractTypes;
    }

    public String getManagerShortName() {
        return managerShortName;
    }

    public String getCuratorShortName() {
        return curatorShortName;
    }

    public String getDirectionName() {
        return directionName;
    }

    public Date getDateSigning() {
        return dateSigning;
    }

    public void setDateSigning(Date dateSigning) {
        this.dateSigning = dateSigning;
    }

    public Date getDateValid() {
        return dateValid;
    }

    public void setDateValid(Date dateValid) {
        this.dateValid = dateValid;
    }

    public List<ContractDate> getContractDates() {
        return contractDates;
    }

    public void setContractDates(List<ContractDate> contractDates) {
        this.contractDates = contractDates;
    }

    public List<ContractSpecification> getContractSpecifications() {
        return contractSpecifications;
    }

    public void setContractSpecifications(List<ContractSpecification> contractSpecifications) {
        this.contractSpecifications = contractSpecifications;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getParentContractId() {
        return parentContractId;
    }

    public void setParentContractId(Long parentContractId) {
        this.parentContractId = parentContractId;
    }

    public String getParentContractNumber() {
        return parentContractNumber;
    }

    public void setParentContractNumber(String parentContractNumber) {
        this.parentContractNumber = parentContractNumber;
    }

    public List<Contract> getChildContracts() {
        return childContracts;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public En_CustomerType getProjectCustomerType() {
        return projectCustomerType;
    }

    public void setProjectCustomerType(En_CustomerType projectCustomerType) {
        this.projectCustomerType = projectCustomerType;
    }

    public Long getCaseDirectionId() {
        return caseDirectionId;
    }

    public String getCaseDirectionName() {
        return caseDirectionName;
    }

    public Long getCaseManagerId() {
        return caseManagerId;
    }

    public String getCaseManagerShortName() {
        return caseManagerShortName;
    }

    public void setCaseManagerId( Long caseManagerId ) {
        this.caseManagerId = caseManagerId;
    }

    public void setCaseDirectionId( Long caseDirectionId ) {
        this.caseDirectionId = caseDirectionId;
    }

    public Long getContractorId() {
        return contractorId;
    }

    public void setContractorId(Long contractorId) {
        this.contractorId = contractorId;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public void setContractor(Contractor contractor) {
        this.contractor = contractor;
    }

    @Override
    public boolean equals(Object obj) {
        if (id != null) {
            return obj instanceof Contract && id.equals(((Contract) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "id=" + id +
                ", refKey='" + refKey + '\'' +
                ", creatorId=" + creatorId +
                ", created=" + created +
                ", modified=" + modified +
                ", managerId=" + managerId +
                ", managerShortName='" + managerShortName + '\'' +
                ", caseManagerId=" + caseManagerId +
                ", caseManagerShortName='" + caseManagerShortName + '\'' +
                ", curatorId=" + curatorId +
                ", curatorShortName='" + curatorShortName + '\'' +
                ", directionId=" + directionId +
                ", directionName='" + directionName + '\'' +
                ", caseDirectionId=" + caseDirectionId +
                ", caseDirectionName='" + caseDirectionName + '\'' +
                ", stateId=" + stateId +
                ", description='" + description + '\'' +
                ", number='" + number + '\'' +
                ", cost=" + cost +
                ", currency=" + currency +
                ", vat=" + vat +
                ", contractTypes=" + contractTypes +
                ", dateSigning=" + dateSigning +
                ", dateValid=" + dateValid +
                ", contractDates=" + contractDates +
                ", contractSpecifications=" + contractSpecifications +
                ", organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                ", parentContractId=" + parentContractId +
                ", parentContractNumber='" + parentContractNumber + '\'' +
                ", childContracts=" + childContracts +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", projectCustomerType=" + projectCustomerType +
                ", contractorId=" + contractorId +
                ", contractor=" + contractor +
                '}';
    }

    @Override
    public EntityOption toEntityOption() {
        return new EntityOption(getNumber(), getId());
    }
}
