package ru.protei.portal.core.model.ent;


import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;

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
    @JdbcColumn
    @JdbcEnumerated(EnumType.STRING)
    private En_EquipmentStage stage;

    /**
     * Тип оборудования
     */
    @JdbcColumn
    @JdbcEnumerated(EnumType.STRING)
    private En_EquipmentType type;

    /**
     * Наименование оборудования (по Solid Works)
     */
    @JdbcColumn
    private String name;

    /**
     * Название по спецификации
     */
    @JdbcColumn(name = "name_by_spec")
    private String nameBySpecification;

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

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getNameBySpecification() {
        return nameBySpecification;
    }

    public void setNameBySpecification( String nameBySpecification ) {
        this.nameBySpecification = nameBySpecification;
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
}
