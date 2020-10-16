package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum  En_WorkTrigger implements HasId {
    /**
     *   Нет
     */
    NONE(0),

    /**
     * ПСГО
     */
    PSGO(1),

    /**
     * Новые требования
     */
    NEW_REQUIREMENTS(2),

    /**
     * Контракт ПНР
     */
    PRE_COMMISSIONING_CONTRACT(3),

    /**
     * Доп.договоренности ПНР
     */
    NEW_PRE_COMMISSIONING_REQUIREMENTS(4),

    /**
     * Маркетинг
     */
    MARKETING(5),

    /**
     * Иное
     */
    OTHER(6);

    En_WorkTrigger( int id ) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public static En_WorkTrigger findById(int id) {
        for (En_WorkTrigger value : En_WorkTrigger.values()) {
            if (value.getId() == id) {
                return value;
            }
        }

        return null;
    }

    private final int id;
}
