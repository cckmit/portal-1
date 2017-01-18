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
    SUPPORT_FINISHED(7);

    private En_RegionState( int id ) {
        this.id = id;
    }

    private int id;

    public int getId() {
        return id;
    }
}
