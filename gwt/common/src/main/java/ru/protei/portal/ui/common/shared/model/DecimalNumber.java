package ru.protei.portal.ui.common.shared.model;

import java.io.Serializable;

/**
 * Децимальный номер оборудования
 */
public class DecimalNumber implements Serializable {

    private OrganizationCode organizationCode;

    private String classifierCode;

    private String registerNumber;

    private String modification;

    public OrganizationCode getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode( OrganizationCode organizationCode ) {
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

    public DecimalNumber( OrganizationCode organizationCode, String classifierCode, String registerNumber, String modification ) {
        this.organizationCode = organizationCode;
        this.classifierCode = classifierCode;
        this.registerNumber = registerNumber;
        this.modification = modification;
    }

    public DecimalNumber() {}
}
