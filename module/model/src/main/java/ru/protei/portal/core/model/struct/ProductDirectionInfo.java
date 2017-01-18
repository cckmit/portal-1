package ru.protei.portal.core.model.struct;

import java.io.Serializable;

/**
 * Информация о продуктовом направлении
 */
public class ProductDirectionInfo implements Serializable {
    public ProductDirectionInfo() {
    }

    public ProductDirectionInfo( Long id, String name ) {
        this.id = id;
        this.name = name;
    }

    /**
     * Идентификатор записи о продуктовом направлении
     */
    public Long id;

    /**
     * Название продуктового направления
     */
    public String name;
}
