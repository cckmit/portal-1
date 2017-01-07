package ru.protei.portal.core.model.dict;

/**
 * Состояние региона
 */
public enum En_RegionState {

    /**
     * Состояние неизвестно
     */
    UNKNOWN(0),

    /**
     * Установлен конкурент
     */
    RIVAL(1),

    /**
     * Идут переговоры
     */
    TALK(2),

    /**
     * Проектирование (документы)
     */
    PROJECTING(3),

    /**
     * Разработка
     */
    DEVELOPMENT(4),

    /**
     * Установка/настройка/сертификация
     */
    DEPLOYMENT(5),

    /**
     * Поддержка
     */
    SUPPORT(6),

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
