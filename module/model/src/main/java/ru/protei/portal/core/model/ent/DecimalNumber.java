package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

/**
 * Децимальный номер оборудования
 */
@JdbcEntity(table = "decimal_number")
public class DecimalNumber implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    /**
     * Регистрационный код организации
     */
    @JdbcColumn( name = "dev_stage" )
    @JdbcEnumerated(EnumType.STRING)
    private En_OrganizationCode organizationCode;

    /**
     * Код по классификатору ЕСКД
     */
    @JdbcColumn( name = "classifier_code" )
    private String classifierCode;

    /**
     * Регистрационный номер
     */
    @JdbcColumn( name = "reg_number" )
    private String registerNumber;

    /**
     * Номер исполнения
     */
    @JdbcColumn( name = "modification_number" )
    private String modification;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public En_OrganizationCode getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode( En_OrganizationCode organizationCode ) {
        this.organizationCode = organizationCode;
    }

    public String getClassifierCode() {
        return classifierCode;
    }

    public void setClassifierCode( String classifierCode ) {
        this.classifierCode = classifierCode;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber( String registerNumber ) {
        this.registerNumber = registerNumber;
    }

    public String getModification() {
        return modification;
    }

    public void setModification( String modification ) {
        this.modification = modification;
    }

    public boolean isValid() {
        return true;
    }

    public DecimalNumber( En_OrganizationCode organizationCode, String classifierCode, String registerNumber, String modification ) {
        this.organizationCode = organizationCode;
        this.classifierCode = classifierCode;
        this.registerNumber = registerNumber;
        this.modification = modification;
    }

    public DecimalNumber() {}
}
