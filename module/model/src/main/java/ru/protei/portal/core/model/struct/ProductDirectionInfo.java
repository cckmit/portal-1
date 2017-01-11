package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_RegionState;

import java.io.Serializable;

/**
 * Информация о продуктовом направлении
 */
public class ProductDirectionInfo implements Serializable {

    /**
     * Идентификатор записи о продуктовом направлении
     */
    public Long id;

    /**
     * Название продуктового направления
     */
    public String name;
}
