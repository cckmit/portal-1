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
    MARKETING(23),

    /**
     * Идут переговоры
     */
    PRESALE(24),

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
     * Тестирование
     */
    TESTING(28),

    /**
     * Поддержка
     */
    SUPPORT(29),

    /**
     * Поддержка закончилась
     */
    FINISHED(32),

    CANCELED(33),
    /**
     * Приостановлена пользователем до даты
     */
    PAUSED(34)
    ;


    private En_RegionState( long id ) {
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

    private long id;

    public long getId() {
        return id;
    }
}
