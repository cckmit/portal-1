package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_RegionState;

import java.io.Serializable;

/**
 * Информация о регионе
 */
public class RegionInfo implements Serializable {

    /**
     * Идентификатор записи о регионе
     */
    public Long id;

    /**
     * Название региона
     */
    public String name;

    /**
     * Состояние региона
     */
    public En_RegionState state;

    /**
     * Дополнительная детальная информация о состоянии
     */
    public String details;

    /**
     * Номер региона
     */
    public Integer number;
}
