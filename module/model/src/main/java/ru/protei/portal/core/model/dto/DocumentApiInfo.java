package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.dict.En_DocumentExecutionType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DocumentApiInfo implements Serializable {
    /**
     * Наименование документа
     */
    private String name;

    /**
     * Обозначение Документа
     */
    private String decimalNumber;

    /**
     * Инвентарный номер
     */
    private Long inventoryNumber;

    /**
     * Вид документа
     */
    private Long typeId;

    /**
     * Аннотация
     */
    private String annotation;

    /**
     * Ответственный за регистрацию
     */
    private Long registrarId;

    /**
     * Исполнитель
     */
    private Long contractorId;

    private Long projectId;

    private String projectLocation;

    private Long equipmentId;

    private String version;

    /**
     * Ключевые слова для поиска
     */
    private List<String> keywords;

    /**
     * Утвержденный
     */
    private Boolean isApproved;

    /**
     * Утвердил
     */
    private Long approvedById;

    /**
     * Дата утверждения
     */
    private Date approvalDate;

    private En_DocumentExecutionType executionType;

    private List<Long> memberIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDecimalNumber() {
        return decimalNumber;
    }

    public void setDecimalNumber(String decimalNumber) {
        this.decimalNumber = decimalNumber;
    }

    public Long getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(Long inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Long getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(Long registrarId) {
        this.registrarId = registrarId;
    }

    public Long getContractorId() {
        return contractorId;
    }

    public void setContractorId(Long contractorId) {
        this.contractorId = contractorId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public Long getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(Long approvedById) {
        this.approvedById = approvedById;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public En_DocumentExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(En_DocumentExecutionType executionType) {
        this.executionType = executionType;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }
}
