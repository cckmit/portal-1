package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.ContractDates;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Договор
 */
@JdbcEntity(table = "Contract")
public class Contract extends AuditableObject implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    /**
     * Создатель анкеты
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
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "MANAGER", sqlTableAlias = "CO")
    private Long managerId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "MANAGER", remoteColumn = "id", table = "Person")
    }, mappedColumn = "displayShortName")
    private String managerShortName;

    /**
     * Куратор договора
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "INITIATOR", sqlTableAlias = "CO")
    private Long curatorId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "INITIATOR", remoteColumn = "id", table = "Person")
    }, mappedColumn = "displayShortName")
    private String curatorShortName;

    /**
     * Контрагент (компания)
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "initiator_company", sqlTableAlias = "CO")
    private Long contragentId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "initiator_company", remoteColumn = "id", table = "Company")
    }, mappedColumn = "cname")
    private String contragentName;

    /**
     * Направление
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "product_id", sqlTableAlias = "CO")
    private Long directionId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "product_id", remoteColumn = "id", table = "dev_unit")
    }, mappedColumn = "UNIT_NAME")
    private String directionName;

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
    @JdbcColumn(name = "cost")
    private Long cost;

    /**
     * Валюта
     */
    @JdbcColumn(name = "cost_currency")
    @JdbcEnumerated(EnumType.ID)
    private En_Currency currency;

    /**
     * Тип
     */
    @JdbcColumn(name = "contract_type")
    @JdbcEnumerated(EnumType.ORDINAL)
    private En_ContractType contractType;

    @JdbcColumn(name = "date_signing")
    private Date dateSigning;

    @JdbcColumn(name = "date_valid")
    private Date dateValid;

    @JdbcColumn(name = "contract_dates", converterType = ConverterType.JSON)
    private ContractDates contractDates;

    @JdbcColumn(name = "organization_id")
    private Long organizationId;

    @JdbcJoinedColumn(localColumn = "organization_id", table = "company", remoteColumn = "id", mappedColumn = "cname")
    private String organizationName;

    @JdbcColumn(name = "parent_contract_id")
    private Long parentContractId;

    // winter не поддерживает JdbcJoinedObject на ту же сущность во избежание рекурсии
    private String parentContractNumber;

    @JdbcOneToMany(table = "Contract", localColumn = "id", remoteColumn = "parent_contract_id")
    private List<Contract> childContracts;


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

    public Long getContragentId() {
        return contragentId;
    }

    public void setContragentId(Long contragentId) {
        this.contragentId = contragentId;
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

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public En_Currency getCurrency() {
        return currency;
    }

    public void setCurrency(En_Currency currency) {
        this.currency = currency;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public En_ContractType getContractType() {
        return contractType;
    }

    public void setContractType(En_ContractType contractType) {
        this.contractType = contractType;
    }

    public String getManagerShortName() {
        return managerShortName;
    }

    public String getCuratorShortName() {
        return curatorShortName;
    }

    public String getContragentName() {
        return contragentName;
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

    public ContractDates getContractDates() {
        return contractDates;
    }

    public void setContractDates(ContractDates contractDates) {
        this.contractDates = contractDates;
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

    @Override
    public String toString() {
        return "Contract{" +
                "id=" + id +
                ", creatorId=" + creatorId +
                ", created=" + created +
                ", modified=" + modified +
                ", managerId=" + managerId +
                ", managerShortName='" + managerShortName + '\'' +
                ", curatorId=" + curatorId +
                ", curatorShortName='" + curatorShortName + '\'' +
                ", contragentId=" + contragentId +
                ", contragentName='" + contragentName + '\'' +
                ", directionId=" + directionId +
                ", directionName='" + directionName + '\'' +
                ", stateId=" + stateId +
                ", description='" + description + '\'' +
                ", number='" + number + '\'' +
                ", cost=" + cost +
                ", currency=" + currency +
                ", contractType=" + contractType +
                ", dateSigning=" + dateSigning +
                ", dateValid=" + dateValid +
                ", contractDates=" + contractDates +
                ", organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                ", parentContractId=" + parentContractId +
                ", parentContractNumber='" + parentContractNumber + '\'' +
                ", childContracts=" + childContracts +
                '}';
    }
}
