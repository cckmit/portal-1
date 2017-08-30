package ru.protei.portal.core.model.query;

/**
 * Created by serebryakov on 30/08/17.
 */
public class OfficialQuery extends BaseQuery {

    private Long productId;

    private Long regionId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
}
