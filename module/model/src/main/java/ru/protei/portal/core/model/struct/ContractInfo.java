package ru.protei.portal.core.model.struct;

import java.io.Serializable;

public class ContractInfo implements Serializable {

    public ContractInfo() {
    }

    public ContractInfo(Long id, String number, String organizationName) {
        this.id = id;
        this.number = number;
        this.organizationName = organizationName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    Long id;
    String number;
    String organizationName;
}
