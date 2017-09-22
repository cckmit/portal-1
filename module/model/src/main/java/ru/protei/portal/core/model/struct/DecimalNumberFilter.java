package ru.protei.portal.core.model.struct;

import java.io.Serializable;
import java.util.List;

/**
 * Created by serebryakov on 22/09/17.
 */
public class DecimalNumberFilter implements Serializable {

    private String organizationCode;

    private List<Integer> excludeNumbers;

    private String classifierCode;

    private String registerNumber;


    public void setClassifierCode(String classifierCode) {
        this.classifierCode = classifierCode;
    }

    public void setOrganizationCode(String organizationCode) { this.organizationCode = organizationCode;
    }

    public void setExcludeNumbers(List<Integer> excludeNumbers) {
        this.excludeNumbers = excludeNumbers;
    }

    public List<Integer> getExcludeNumbers() {
        return excludeNumbers;
    }

    public String getClassifierCode() {
        return classifierCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }
}
