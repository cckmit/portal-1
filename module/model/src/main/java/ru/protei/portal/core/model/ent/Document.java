package ru.protei.portal.core.model.ent;

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
    @JdbcColumn
    private String project;

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

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
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
}
