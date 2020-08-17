package ru.protei.portal.core.model.struct;

import java.io.Serializable;
import java.util.Objects;

/**
 * Пример: 145.63 (145 рублей 63 копейки) (145 долларов 63 цента)
 * <ul>
 *     <li>[natural] - целая часть суммы (145)</li>
 *     <li>[decimal] - дробная часть суммы (63)</li>
 *     <li>[full] - полная сумма (14563)</li>
 * </ul>
 */
public class Money implements Serializable {

    private Long value;

    public Money() {
    }

    public Money(Long natural, Long decimal) {
        value = safeNaturalToFull(natural) + safeDecimalToFull(decimal);
    }

    public Money(Long full) {
        value = safeFull(full);
    }

    public long getNatural() {
        return value / 100;
    }

    public void setNatural(Long natural) {
        value = safeNaturalToFull(natural) + value % 100;
    }

    public long getDecimal() {
        return value % 100;
    }

    public void setDecimal(Long decimal) {
        value = value / 100 + safeDecimalToFull(decimal);
    }

    public long getFull() {
        return value;
    }

    public void setFull(Long full) {
        value = safeFull(full);
    }

    private long safeFull(Long full) {
        return full != null ? full : 0;
    }

    private long safeNaturalToFull(Long natural) {
        return natural != null
                ? natural * 100
                : 0;
    }

    private long safeDecimalToFull(Long decimal) {
        return decimal != null
                ? decimal % 100
                : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money currency = (Money) o;
        return Objects.equals(value, currency.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        long natural = getNatural();
        long decimal = getDecimal();
        return natural + "." + (decimal < 10 ? "0" + decimal : decimal);
    }
}
