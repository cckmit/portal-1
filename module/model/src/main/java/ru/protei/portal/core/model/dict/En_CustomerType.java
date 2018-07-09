package ru.protei.portal.core.model.dict;

public enum En_CustomerType {
    /**
     * Министерство обороны
     */
    MINISTRY_OF_DEFENCE,

    /**
     * Госбюджет
     */
    STATE_BUDGET,

    /**
     * Коммерческое РФ
     */
    COMMERCIAL_RF,

    /**
     * Коммерческое ближнее зарубежье
     */
    COMMERCIAL_NEAR_ABROAD,

    /**
     * Коммерческое дальнее зарубежье
     */
    COMMERCIAL_FAR_ABROAD,

    /**
     * Коммерческое ПРОТЕЙ
     */
    COMMERCIAL_PROTEI;


    public static En_CustomerType forId(int id) {
        for (En_CustomerType type : En_CustomerType.values()) {
            if (type.ordinal() == id)
                return type;
        }
        return null;
    }
}
