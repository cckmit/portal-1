package ru.protei.portal.core.model.dict;

/**
 * Область видимости роли
 */
public enum En_Scope {

    /**
     * Область видимости – компания авторизованного пользователя
     */
    COMPANY(1),

    /**
     * Область видимости – все компании + ограничения на создание логинов – только логины всешних закачиков
     */
    LOCAL(2),

    /**
     * Область видимости – все сущности системы
     */
    SYSTEM(3);

    /**
     * Вес области видимости
     */
    private int weight;

    En_Scope( int weight ) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
