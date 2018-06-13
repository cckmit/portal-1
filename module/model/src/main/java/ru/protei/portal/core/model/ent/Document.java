package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.ProjectInfo;
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

    /**
     * Децимальный номер
     */
    @JdbcJoinedObject(localColumn = "decimal_number_id")
    private DecimalNumber decimalNumber;


    /**
     * Инвентарный номер
     */
    @JdbcColumn(name = "inventory_number")
    private Long inventoryNumber;

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
     * Менеджер
     */
    @JdbcColumn(name = "manager_id")
    private Long managerId;
    @JdbcJoinedColumn(localColumn = "manager_id", table = "Person", remoteColumn = "id", mappedColumn = "displayShortName")
    private String managerShortName;

    @JdbcColumn(name = "project_id")
    private Long projectId;
    @JdbcJoinedColumn(localColumn = "project_id", table = "case_object", remoteColumn = "id", mappedColumn = "case_name")
    private String projectName;
    @JdbcJoinedObject(localColumn = "project_id", table = "case_object", remoteColumn = "id")
    private ProjectInfo projectInfo;

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
     * Код вида документа
     */
    @JdbcColumn(name = "type_code")
    private String typeCode;

    public Document() {
    }

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

    public String getManagerShortName() {
        return managerShortName;
    }

    public void setManagerShortName(String managerShortName) {
        this.managerShortName = managerShortName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public DecimalNumber getDecimalNumber() {
        return decimalNumber;
    }

    public void setDecimalNumber(DecimalNumber decimalNumber) {
        this.decimalNumber = decimalNumber;
    }

    public boolean isValid() {
        return  this.getType() != null &&
                this.getManagerId() != null &&
                this.getInventoryNumber() != null &&
                this.getInventoryNumber() > 0 &&
                this.getProjectId() != null &&
                HelperFunc.isNotEmpty(this.getName());
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(ProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
