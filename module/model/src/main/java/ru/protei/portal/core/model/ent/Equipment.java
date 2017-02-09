package ru.protei.portal.core.model.ent;


import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

/**
 * Оборудование
 */
@JdbcEntity(table = "Equipment")
public class Equipment implements Serializable {

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
     * Код по классификатору ЕСКД
     */
    @JdbcColumn(name = "classifier_code")
    private String classifierCode;

    /**
     * Регистрационный номер в компании "Протей"
     */
    @JdbcColumn(name = "pamr_reg_num")
    private String PAMR_RegisterNumber;

    /**
     * Регистрационный номер в компании "Протей СТ"
     */
    @JdbcColumn(name = "pdra_reg_num")
    private String PDRA_RegisterNumber;

    /**
     * Комментарий к оборудованию
     */
    @JdbcColumn
    private String comment;

    /**
     * Первичное применение
     */
    @JdbcJoinedObject(localColumn = "linked_equipment_id", remoteColumn = "id", updateLocalColumn = true )
    private Equipment linkedEquipment;

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

    public String getClassifierCode() {
        return classifierCode;
    }

    public void setClassifierCode( String classifierCode ) {
        this.classifierCode = classifierCode;
    }

    public String getPAMR_RegisterNumber() {
        return PAMR_RegisterNumber;
    }

    public void setPAMR_RegisterNumber( String PAMR_RegisterNumber ) {
        this.PAMR_RegisterNumber = PAMR_RegisterNumber;
    }

    public String getPDRA_RegisterNumber() {
        return PDRA_RegisterNumber;
    }

    public void setPDRA_RegisterNumber( String PDRA_RegisterNumber ) {
        this.PDRA_RegisterNumber = PDRA_RegisterNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment( String comment ) {
        this.comment = comment;
    }

    public Equipment getLinkedEquipment() {
        return linkedEquipment;
    }

    public void setLinkedEquipment( Equipment linkedEquipment ) {
        this.linkedEquipment = linkedEquipment;
    }
}
