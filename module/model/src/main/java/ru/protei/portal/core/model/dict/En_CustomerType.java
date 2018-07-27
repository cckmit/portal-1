package ru.protei.portal.core.model.dict;

public enum En_CustomerType {
    /**
     * Министерство обороны
     */
    MINISTRY_OF_DEFENCE(1),

    /**
     * Госбюджет
     */
    STATE_BUDGET(2),

    /**
     * Коммерческое РФ
     */
    COMMERCIAL_RF(3),

    /**
     * Коммерческое ближнее зарубежье
     */
    COMMERCIAL_NEAR_ABROAD(4),

    /**
     * Коммерческое дальнее зарубежье
     */
    COMMERCIAL_FAR_ABROAD(5),

    /**
     * Коммерческое ПРОТЕЙ
     */
    COMMERCIAL_PROTEI(6);

    En_CustomerType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static En_CustomerType find(int id) {
        for (En_CustomerType type : En_CustomerType.values()) {
            if (type.id == id)
                return type;
        }
        return null;
    }

    private final int id;
}
