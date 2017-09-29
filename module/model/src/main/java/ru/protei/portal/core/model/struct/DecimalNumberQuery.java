package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_OrganizationCode;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Фильтр при запросе модификаций/исполнений для децимальных номеров
 */
public class DecimalNumberQuery implements Serializable {

    private En_OrganizationCode organizationCode;

    private Set<Integer> excludeNumbers;

    private Integer classifierCode;

    private Integer registerNumber;

    public void setOrganizationCode(En_OrganizationCode organizationCode) { this.organizationCode = organizationCode;}

    public void setExcludeNumbers(Set<Integer> excludeNumbers) {
        this.excludeNumbers = excludeNumbers;
    }

    public Set<Integer> getExcludeNumbers() {
        return excludeNumbers;
    }

    public Integer getClassifierCode() {
        return classifierCode;
    }

    public void setClassifierCode(Integer classifierCode) {
        this.classifierCode = classifierCode;
    }

    public En_OrganizationCode getOrganizationCode() {
        return organizationCode;
    }

    public void setRegisterNumber(Integer registerNumber) {
        this.registerNumber = registerNumber;
    }

    public Integer getRegisterNumber() {
        return registerNumber;
    }
}
