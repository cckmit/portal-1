package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.marker.HasLongId;
import ru.protei.portal.core.model.view.EntityOption;

import java.io.Serializable;

/**
 * Информация о продуктовом направлении
 */
public class ProductDirectionInfo implements Serializable, HasLongId {
    public ProductDirectionInfo() {
    }

    public ProductDirectionInfo(Long id) {
        this.id = id;
    }

    public ProductDirectionInfo(Long id, String name ) {
        this.id = id;
        this.name = name;
    }

    public ProductDirectionInfo( EntityOption entityOption ) {
        this.id = entityOption.getId();
        this.name = entityOption.getDisplayText();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        ProductDirectionInfo that = (ProductDirectionInfo) o;

        return id != null ? id.equals( that.id ) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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
