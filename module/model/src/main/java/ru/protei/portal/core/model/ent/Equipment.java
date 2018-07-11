package ru.protei.portal.core.model.ent;


import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Оборудование
 */
@JdbcEntity(table = "Equipment")
public class Equipment extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    /**
     * Стадии разработки оборудования
     */
    @JdbcColumn( name = "dev_stage" )
    @JdbcEnumerated(EnumType.STRING)
    private En_EquipmentStage stage;

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

    /**
     * Менеджер
     */
    @JdbcColumn( name = "manager_id" )
    private Long managerId;

    @JdbcJoinedColumn( localColumn = "manager_id", table = "Person", remoteColumn = "id", mappedColumn = "displayShortName")
    private String managerShortName;

    /**
     * Проект
     */
    @JdbcJoinedObject(localColumn = "project_id")
    private CaseObject projectCaseObject;

    /**
     * Привязанные децимальные номера
     */
    @JdbcOneToMany(table = "decimal_number", localColumn = "id", remoteColumn = "entity_id")
    private List<DecimalNumber> decimalNumbers;

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
        this.stage = equipment.getStage();
        this.type = equipment.getType();
        this.managerId = equipment.getManagerId();
        this.projectCaseObject = equipment.getProjectCaseObject();
        this.comment = equipment.getComment();
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public En_EquipmentStage getStage() {
        return stage;
    }

    public void setStage( En_EquipmentStage stage ) {
        this.stage = stage;
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

    public ProjectInfo getProject() {
        return ProjectInfo.fromCaseObject(getProjectCaseObject());
    }

    public void setProject(ProjectInfo project) {
        setProjectCaseObject(project == null ? null : new CaseObject(project.getId()));
    }

    public CaseObject getProjectCaseObject() {
        return projectCaseObject;
    }

    public void setProjectCaseObject(CaseObject projectCaseObject) {
        this.projectCaseObject = projectCaseObject;
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
