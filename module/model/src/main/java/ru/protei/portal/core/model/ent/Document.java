package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DocumentExecutionType;
import ru.protei.portal.core.model.dict.En_DocumentState;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Документ
 * элемент раздела "Банк документов"
 */
@JdbcEntity(table = "document")
public class Document implements Serializable {

    @JdbcId(idInsertMode = IdInsertMode.AUTO)
    private Long id;

    /**
     * Наименование документа
     */
    @JdbcColumn
    private String name;

    @JdbcColumn(name = "decimal_number")
    private String decimalNumber;

    /**
     * Инвентарный номер
     */
    @JdbcColumn(name = "inventory_number")
    private Long inventoryNumber;

    /**
     * Состояние
     */
    @JdbcColumn(name = "state")
    @JdbcEnumerated(EnumType.ID)
    private En_DocumentState state;

    /**
     * Вид документа
     */
    @JdbcJoinedObject(localColumn = "type_id")
    private DocumentType type;

    /**
     * Аннотация
     */
    @JdbcColumn
    private String annotation;

    /**
     * Ответственный за регистрацию
     */
    @JdbcJoinedObject(localColumn = "registrar_id")
    private Person registrar;

    /**
     * Исполнитель
     */
    @JdbcJoinedObject(localColumn = "contractor_id")
    private Person contractor;

    @JdbcColumn(name = "project_id")
    private Long projectId;

    @JdbcJoinedColumn(localColumn = "project_id", table = "case_object", remoteColumn = "id", mappedColumn = "case_name", sqlTableAlias = "case_object")
    private String projectName;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "project_id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "initiator_company", remoteColumn = "id", table = "company")
    }, mappedColumn = "cname")
    private String contragentName;

    @JdbcJoinedObject(localColumn = "equipment_id")
    private Equipment equipment;

    @JdbcColumn(name = "version")
    private String version;

    /**
     * Дата создания
     */
    @JdbcColumn
    private Date created = new Date();

    /**
     * Ключевые слова для поиска
     */
    @JdbcColumnCollection(name = "tags", separator = ",")
    private List<String> keywords;

    /**
     * Утвержденный
     */
    @JdbcColumn(name = "is_approved")
    private Boolean isApproved;

    /**
     * Утвердил
     */
    @JdbcJoinedObject(localColumn = "approved_by_id")
    private Person approvedBy;

    /**
     * Дата создания
     */
    @JdbcColumn(name = "approval_date")
    private Date approvalDate;

    @JdbcColumn(name = "execution_type")
    @JdbcEnumerated(EnumType.ORDINAL)
    private En_DocumentExecutionType executionType;

    @JdbcManyToMany(linkTable = "document_member", localLinkColumn = "document_id", remoteLinkColumn = "person_id")
    private List<Person> members;

    public Document(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(Long inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getDecimalNumber() {
        return decimalNumber;
    }

    public void setDecimalNumber(String decimalNumberStr) {
        this.decimalNumber = decimalNumberStr;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
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

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName( String contragentName ) {
        this.contragentName = contragentName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Person getRegistrar() {
        return registrar;
    }

    public void setRegistrar(Person registrar) {
        this.registrar = registrar;
    }

    public Person getContractor() {
        return contractor;
    }

    public void setContractor(Person contractor) {
        this.contractor = contractor;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public Person getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Person approvedBy) {
        this.approvedBy = approvedBy;
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

    public void setState(En_DocumentState state) {
        this.state = state;
    }

    public En_DocumentState getState(){
        return state;
    }

    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    public boolean isActiveUnit () {
        return getState() == En_DocumentState.ACTIVE;
    }

    public boolean isDeprecatedUnit() {
        return getState() == En_DocumentState.DEPRECATED;
    }

    public boolean isValid() {
        // Основная проверка, дополнительные проверки обрабатываются в клиенте и сервере отдельно
        return  this.getType() != null &&
                (this.getInventoryNumber() == null || (this.getInventoryNumber() > 0)) &&
                this.getProjectId() != null &&
                HelperFunc.isNotEmpty(this.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Document) {
            Long oid = ((Document) obj).getId();
            return id == null ? oid == null : id.equals(oid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", decimalNumber='" + decimalNumber + '\'' +
                ", inventoryNumber=" + inventoryNumber +
                ", type=" + type +
                ", annotation='" + annotation + '\'' +
                ", registrar=" + registrar +
                ", contractor=" + contractor +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", contragentName='" + contragentName + '\'' +
                ", equipment=" + equipment +
                ", version='" + version + '\'' +
                ", created=" + created +
                ", keywords=" + keywords +
                ", isApproved=" + isApproved +
                ", approvedBy=" + approvedBy +
                ", approvalDate =" + approvalDate +
                ", executionType=" + executionType +
                ", state=" + state +
                ", members=" + members +
                '}';
    }
}
