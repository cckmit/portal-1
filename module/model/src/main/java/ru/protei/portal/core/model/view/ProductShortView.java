package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.io.Serializable;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.toSet;

/**
 * Сокращенное представление продукта
 */
public class ProductShortView implements Serializable {
    private Long id;
    private String name;
    private int stateId;
    private String aliases;
    private En_DevUnitType type;
    private Set<ProductDirectionInfo> productDirection;

    public ProductShortView() {
    }

    public ProductShortView( Long id, String name, int stateId ) {
        this.id = id;
        this.name = name;
        this.stateId = stateId;
    }

    public ProductShortView( Long id, String name, int stateId, String aliases, En_DevUnitType type, Set<ProductDirectionInfo> productDirection ) {
        this.id = id;
        this.name = name;
        this.stateId = stateId;
        this.aliases = aliases;
        this.type = type;
        this.productDirection = productDirection;
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

    public String getAliases() {
        return aliases;
    }

    public En_DevUnitType getType() {
        return type;
    }

    public void setType(En_DevUnitType type) {
        this.type = type;
    }

    public Set<ProductDirectionInfo> getProductDirection() {
        return productDirection;
    }

    public void setProductDirection(Set<ProductDirectionInfo> productDirection) {
        this.productDirection = productDirection;
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
        if(product == null) return null;
        return new ProductShortView(
                product.getId(),
                product.getName(),
                product.getStateId(),
                CollectionUtils.isEmpty(product.getAliases()) ? "" : String.join(", ", product.getAliases()),
                product.getType(),
                product.getProductDirections() == null ? null : toSet(product.getProductDirections(), DevUnit::toProductDirectionInfo));
    }

    @Override
    public String toString() {
        return name;
    }
}
