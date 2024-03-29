package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Оборудование
 */
@JdbcEntity(table = "equipment")
public class Equipment extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    /**
     * Тип оборудования
     */
    @JdbcColumn
    @JdbcEnumerated(EnumType.STRING)
    private En_EquipmentType type;

    /**
     * Название по спецификации
     */
    @JdbcColumn
    private String name;

    /**
     * Наименование оборудования (по Solid Works)
     */
    @JdbcColumn(name = "name_sldwrks")
    private String nameSldWrks;

    /**
     * Комментарий к оборудованию
     */
    @JdbcColumn
    private String comment;

    /**
     * Дата создания
     */
    @JdbcColumn
    private Date created;

    /**
     * Автор
     */
    @JdbcColumn( name = "author_id" )
    private Long authorId;

    @JdbcJoinedColumn( localColumn = "author_id", table = "person", remoteColumn = "id", mappedColumn = "displayShortName")
    private String authorShortName;

    /**
     * Менеджер
     */
    @JdbcColumn( name = "manager_id" )
    private Long managerId;

    @JdbcJoinedColumn( localColumn = "manager_id", table = "person", remoteColumn = "id", mappedColumn = "displayShortName")
    private String managerShortName;

    /**
     * Проект
     */
    @JdbcColumn(name = "project_id")
    private Long projectId;

    @JdbcJoinedColumn(localColumn = "project_id", table = "case_object", remoteColumn = "id", mappedColumn = "CASE_NAME", sqlTableAlias = "case_object")
    private String projectName;

    /**
     * Привязанные децимальные номера
     */
    @JdbcOneToMany(table = "decimal_number", localColumn = "id", remoteColumn = "entity_id")
    private List<DecimalNumber> decimalNumbers;

    // специальная вью со обработанным номером для сортировки (classifierCode + registerNumber)
    @JdbcJoinedColumn(mappedColumn = "sort_decimal",
            table = "view_equipments_decimal_number_sort", sqlTableAlias = "decimal_view" ,
            localColumn = "id",remoteColumn = "Equipment_id")
    private String sortDecimal;

    /**
     * Первичное применение
     */
    @JdbcColumn(name = "linked_equipment_id")
    private Long linkedEquipmentId;

    @JdbcOneToMany(table = "decimal_number", localColumn = "linked_equipment_id", remoteColumn = "entity_id")
    private List<DecimalNumber> linkedEquipmentDecimalNumbers;

    public Equipment() {
    }

    public Equipment( Long id ) {
        this.id = id;
    }

    public Equipment( Equipment equipment ) {
        this.name = equipment.getName();
        this.nameSldWrks = equipment.getNameSldWrks();
        this.type = equipment.getType();
        this.managerId = equipment.getManagerId();
        this.comment = equipment.getComment();
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public En_EquipmentType getType() {
        return type;
    }

    public void setType( En_EquipmentType type ) {
        this.type = type;
    }

    public String getNameSldWrks() {
        return nameSldWrks;
    }

    public void setNameSldWrks( String nameSldWrks ) {
        this.nameSldWrks = nameSldWrks;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<DecimalNumber> getDecimalNumbers() {
        return decimalNumbers;
    }

    public void setDecimalNumbers( List<DecimalNumber> decimalNumbers ) {
        this.decimalNumbers = decimalNumbers;
    }

    public void addDecimalNumber( DecimalNumber decimalNumber ) {
        if (this.decimalNumbers == null) {
            this.decimalNumbers = new ArrayList<>();
        }
        this.decimalNumbers.add(decimalNumber);
    }

    public String getComment() {
        return comment;
    }

    public void setComment( String comment ) {
        this.comment = comment;
    }

    public Long getLinkedEquipmentId() {
        return linkedEquipmentId;
    }

    public void setLinkedEquipmentId( Long linkedEquipmentId ) {
        this.linkedEquipmentId = linkedEquipmentId;
    }

    public List< DecimalNumber > getLinkedEquipmentDecimalNumbers() {
        return linkedEquipmentDecimalNumbers;
    }

    public void setLinkedEquipmentDecimalNumbers( List< DecimalNumber > linkedEquipmentDecimalNumbers ) {
        this.linkedEquipmentDecimalNumbers = linkedEquipmentDecimalNumbers;
    }

    public void addLinkedEquipmentDecimalNumber( DecimalNumber decimalNumber ) {
        if (this.linkedEquipmentDecimalNumbers == null) {
            this.linkedEquipmentDecimalNumbers = new ArrayList<>();
        }
        this.linkedEquipmentDecimalNumbers.add(decimalNumber);
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId( Long authorId ) {
        this.authorId = authorId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId( Long managerId ) {
        this.managerId = managerId;
    }

    public String getManagerShortName() {
        return managerShortName;
    }

    public void setManagerShortName( String managerShortName ) {
        this.managerShortName = managerShortName;
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

    public String getAuthorShortName() {
        return authorShortName;
    }

    public String getSortDecimal() {
        return sortDecimal;
    }

    public void setSortDecimal(String sortDecimal) {
        this.sortDecimal = sortDecimal;
    }

    @Override
    public String getAuditType() {
        return "Equipment";
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Equipment ) ) return false;

        Equipment equipment = (Equipment) o;

        return id != null ? id.equals( equipment.id ) : equipment.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
