package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.DecimalNumber;

import java.io.Serializable;
import java.util.Set;

/**
 * Фильтр при запросе модификаций/исполнений для децимальных номеров
 */
public class DecimalNumberQuery implements Serializable {

    private DecimalNumber number;

    private Set<Integer> excludeNumbers;

    public void setExcludeNumbers(Set<Integer> excludeNumbers) {
        this.excludeNumbers = excludeNumbers;
    }

    public Set<Integer> getExcludeNumbers() {
        return excludeNumbers;
    }

    public DecimalNumber getNumber() {
        return number;
    }

    public void setNumber( DecimalNumber number ) {
        this.number = number;
    }

    public DecimalNumberQuery( DecimalNumber number, Set< Integer > excludeNumbers ) {
        this.number = number;
        this.excludeNumbers = excludeNumbers;
    }

    public DecimalNumberQuery() {
    }
}
