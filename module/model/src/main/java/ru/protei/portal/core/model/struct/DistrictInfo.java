package ru.protei.portal.core.model.struct;

import java.io.Serializable;

/**
 * Информация об округе
 */
public class DistrictInfo implements Serializable {

    /**
     * Идентификатор записи о регионе
     */
    public Long id;

    /**
     * Название региона
     */
    public String name;

    /**
     * Аббревиатура (для фильтра)
     */
    public String shortName;
}
