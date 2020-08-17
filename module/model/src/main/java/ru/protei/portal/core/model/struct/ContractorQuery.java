package ru.protei.portal.core.model.struct;

import java.io.Serializable;

public class ContractorQuery implements Serializable {

    private String refKey;
    private String inn;
    private String kpp;
    private String fullName;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "ContractorQuery{" +
                "refKey='" + refKey + '\'' +
                ", inn='" + inn + '\'' +
                ", kpp='" + kpp + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
