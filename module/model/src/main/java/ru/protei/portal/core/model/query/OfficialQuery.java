package ru.protei.portal.core.model.query;

import java.util.Date;

/**
 * Created by serebryakov on 30/08/17.
 */
public class OfficialQuery extends BaseQuery {

    private Long productId;

    private Long regionId;

    private Date from;

    private Date to;

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

    public Date getFrom() { return from; }

    public void setFrom(Date from) { this.from = from; }

    public Date getTo() { return to; }

    public void setTo(Date to) { this.to = to; }
}
