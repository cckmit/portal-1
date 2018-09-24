package ru.protei.portal.core.model.dict;

/**
 * Тип документа
 */
public enum En_DocumentCategory {

    /**
     * Технический проект
     */
    TP(En_DecimalNumberType.ESPD),

    /**
     * Конструкторская документация
     */
    KD(En_DecimalNumberType.ESKD),

    /**
     * Программная документация
     */
    PD(En_DecimalNumberType.ESPD),

    /**
     * Эксплуатационная документация
     */
    ED(En_DecimalNumberType.ED),

    /**
     * Технологическая документация
     */
    TD(En_DecimalNumberType.TD),

    /**
     * Зарубежье
     */
    ABROAD(null);

    En_DocumentCategory(En_DecimalNumberType type) {
        this.type = type;
    }

    public boolean isForEquipment() {
        return En_DecimalNumberType.ESKD.equals(getType()) || En_DecimalNumberType.ED.equals(getType());
    }

    public En_DecimalNumberType getType() {
        return type;
    }

    private final En_DecimalNumberType type;
}
