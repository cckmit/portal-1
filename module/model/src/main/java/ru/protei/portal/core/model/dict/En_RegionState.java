package ru.protei.portal.core.model.dict;

/**
 * Состояние региона
 */
public enum En_RegionState {

    /**
     * Состояние неизвестно
     */
    UNKNOWN(22),

    /**
     * Установлен конкурент
     */
    RIVAL(23),

    /**
     * Идут переговоры
     */
    TALK(24),

    /**
     * Проектирование (документы)
     */
    PROJECTING(25),

    /**
     * Разработка
     */
    DEVELOPMENT(26),

    /**
     * Установка/настройка/сертификация
     */
    DEPLOYMENT(27),

    /**
     * Поддержка
     */
    SUPPORT(28),

    /**
     * Поддержка закончилась
     */
    SUPPORT_FINISHED(29);

    private En_RegionState( int id ) {
        this.id = id;
    }

    public static En_RegionState forId( long stateId ) {
        for ( En_RegionState state : values() ) {
            if ( state.getId() == stateId ) {
                return state;
            }
        }

        return UNKNOWN;
    }

    private int id;

    public int getId() {
        return id;
    }
}
