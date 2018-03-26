package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DecimalNumberEntityType;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Objects;

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
     * Тип сущности, которой принадлежит децимальный номер
     */
    @JdbcColumn(name = "entity_type")
    @JdbcEnumerated(EnumType.STRING)
    private En_DecimalNumberEntityType entityType;

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

    @JdbcColumn(name = "entity_id")
    private Long entityId;

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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public boolean isEmpty() {
        return organizationCode == null
                || ( classifierCode == null || classifierCode < 1 )
                || ( registerNumber == null || registerNumber < 1 );
    }

    public boolean isSameNumber( DecimalNumber number ) {
        return  organizationCode == number.getOrganizationCode()
                && Objects.equals( modification, number.getModification() )
                && Objects.equals( classifierCode, number.getClassifierCode() )
                && Objects.equals( registerNumber, number.getRegisterNumber() );
    }

    public DecimalNumber( En_OrganizationCode organizationCode, Integer classifierCode, Integer registerNumber, Integer modification ) {
        this.organizationCode = organizationCode;
        this.classifierCode = classifierCode;
        this.registerNumber = registerNumber;
        this.modification = modification;
    }

    public boolean isValid() {
        return getClassifierCode() != null &&
                getRegisterNumber() != null &&
                getEntityType() != null;
    }

    public DecimalNumber() {}

    public En_DecimalNumberEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(En_DecimalNumberEntityType entityType) {
        this.entityType = entityType;
    }
}
