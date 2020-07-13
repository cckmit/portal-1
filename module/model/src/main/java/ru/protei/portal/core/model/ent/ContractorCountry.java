package ru.protei.portal.core.model.ent;

import java.io.Serializable;

public class ContractorCountry implements Serializable {
    private String refKey;

    private String name;

    public ContractorCountry() {
    }

    public ContractorCountry(String refKey, String name) {
        this.refKey = refKey;
        this.name = name;
    }

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ContractorCountryAPI{" +
                "refKey='" + refKey + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
