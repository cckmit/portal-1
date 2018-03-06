package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Документ
 * элемент раздела "Банк документов"
 */
@JdbcEntity(table = "documentation")
public class Documentation implements Serializable {

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
    @JdbcColumn(name = "decimal_number_id")
    private DecimalNumber decimalNumber;


    /**
     * Инвентарный номер
     */
    @JdbcColumn(name = "inventory_number")
    private Long inventoryNumber;

    /**
     * Вид документа
     */
    @JdbcColumn(name = "type_id")
    private Long typeId;
    @JdbcJoinedColumn(localColumn = "type_id", table = "document_type", remoteColumn = "id", mappedColumn = "name")
    private String type;

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
    private Date created;

    /**
     * Ключевые слова для поиска
     */
    @JdbcColumn
    @JdbcColumnCollection(separator = ",")
    private Set<String> tags;

    public Documentation() {
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

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
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

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DecimalNumber getDecimalNumber() {
        return decimalNumber;
    }

    public void setDecimalNumber(DecimalNumber decimalNumber) {
        this.decimalNumber = decimalNumber;
    }
}
