package ru.protei.portal.core.model.ent;

import java.io.Serializable;

public class ContractorAPI implements Serializable {
    private String organization;

    private String refKey;

    private String name;

    private String fullname;

    private String inn;

    private String kpp;

    private String country;

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "ContractorAPI{" +
                "organization=" + organization +
                ", refKey='" + refKey + '\'' +
                ", name='" + name + '\'' +
                ", fullname='" + fullname + '\'' +
                ", inn='" + inn + '\'' +
                ", kpp='" + kpp + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
