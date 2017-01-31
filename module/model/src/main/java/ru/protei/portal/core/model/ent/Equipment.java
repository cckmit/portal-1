package ru.protei.portal.core.model.ent;


import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

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

    /**
     * Список единиц, в которые входит
     */
    private List<Equipment> includedIn;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
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
