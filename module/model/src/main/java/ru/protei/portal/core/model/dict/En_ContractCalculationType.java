package ru.protei.portal.core.model.dict;

/**
 * Вид взаиморасчётов
 */
public enum En_ContractCalculationType {

    /**
     * Отдельный счет - Отдельный счет
     */
    OS_OS("0"),

    /**
     * Отдельный счет - Расчетный счет
     */
    OS_PC("1"),

    /**
     * Расчетный счет - Расчетный счет
     */
    PC_PC("2");

    private final String value;

    En_ContractCalculationType(String value) {
        this.value = value;
    }

    public String getStringValue() {
        return value;
    }
}
