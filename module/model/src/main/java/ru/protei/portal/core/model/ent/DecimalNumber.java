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
    @JdbcColumn( name = "org_code" )
    @JdbcEnumerated(EnumType.STRING)
    private En_OrganizationCode organizationCode;

    /**
     * Код по классификатору ЕСКД
     */
    @JdbcColumn( name = "classifier_code" )
    private Integer classifierCode;

    /**
     * Регистрационный номер
     */
    @JdbcColumn( name = "reg_number" )
    private Integer registerNumber;

    /**
     * Номер исполнения
     */
    @JdbcColumn( name = "modification_number" )
    private Integer modification;

    @JdbcColumn( name = "equipment_id")
    private Long equipmentId;

    @JdbcColumn( name = "is_reserve")
    private boolean isReserve;

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

    public Integer getClassifierCode() {
        return classifierCode;
    }

    public void setClassifierCode( Integer classifierCode ) {
        this.classifierCode = classifierCode;
    }

    public Integer getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber( Integer registerNumber ) {
        this.registerNumber = registerNumber;
    }

    public Integer getModification() {
        return modification;
    }

    public void setModification( Integer modification ) {
        this.modification = modification;
    }

    public boolean isReserve() {
        return isReserve;
    }

    public void setReserve( boolean reserve ) {
        isReserve = reserve;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId( Long equipmentId ) {
        this.equipmentId = equipmentId;
    }

    public boolean isValid() {
        return true;
    }

    public DecimalNumber( En_OrganizationCode organizationCode, Integer classifierCode, Integer registerNumber, Integer modification ) {
        this.organizationCode = organizationCode;
        this.classifierCode = classifierCode;
        this.registerNumber = registerNumber;
        this.modification = modification;
    }

    public DecimalNumber() {}
}
