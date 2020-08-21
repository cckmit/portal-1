package ru.protei.portal.core.model.struct;

import java.io.Serializable;

public class ContractorQuery implements Serializable {

    private String refKey;
    private String inn;
    private String kpp;
    private String name;
    private String fullName;
    private String registrationCountryKey;

    public ContractorQuery() {
    }

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRegistrationCountryKey() {
        return registrationCountryKey;
    }

    public void setRegistrationCountryKey(String registrationCountryKey) {
        this.registrationCountryKey = registrationCountryKey;
    }

    @Override
    public String toString() {
        return "ContractorQuery{" +
                "refKey='" + refKey + '\'' +
                ", inn='" + inn + '\'' +
                ", kpp='" + kpp + '\'' +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", registrationCountryKey='" + registrationCountryKey + '\'' +
                '}';
    }
}
