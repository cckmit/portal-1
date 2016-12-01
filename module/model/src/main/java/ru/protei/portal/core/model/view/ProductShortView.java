package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.ent.DevUnit;

import java.io.Serializable;

/**
 * Сокращенное представление продукта
 */
public class ProductShortView implements Serializable {
    private Long id;
    private String name;
    private int stateId;

    public ProductShortView() {
    }

    public ProductShortView( Long id, String name, int stateId ) {
        this.id = id;
        this.name = name;
        this.stateId = stateId;
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId( int stateId ) {
        this.stateId = stateId;
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof ProductShortView) {
            Long oid = ((ProductShortView)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static ProductShortView fromProduct( DevUnit product ) {
        if(product == null)
            return null;
        return new ProductShortView( product.getId(), product.getName(), product.getStateId() );
    }
}
