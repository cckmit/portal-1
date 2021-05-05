package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.DateRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

public class DeliveryQuery extends BaseQuery implements HasFilterQueryIds {

    @JsonIgnore
    private Long id;

    private String name;

    private DateRange deliveryRange;

    private List<Long> companyIds;

    private Set<Long> productIds;

    private List<Long> managerIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateRange getDeliveryRange() {
        return deliveryRange;
    }

    public void setDeliveryRange(DateRange deliveryRange) {
        this.deliveryRange = deliveryRange;
    }

    public DeliveryQuery() {}

    public DeliveryQuery(Long id) {
        setId(id);
    }

    public DeliveryQuery(DeliveryQuery query) {
        setSearchString(query.getSearchString());
        setSortField(query.getSortField());
        setSortDir(query.getSortDir());

        setId(query.getId());
        setName(query.getName());
        setDeliveryRange(query.getDeliveryRange());
        setCompanyIds(query.getCompanyIds());
        setProductIds(query.getProductIds());
        setManagerIds(query.getManagerIds());
    }

    public Set<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds( Set<Long> productIds ) { this.productIds = productIds; }

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds;
    }

    public List<Long> getManagerIds() {
        return managerIds;
    }

    public void setManagerIds(List<Long> managerIds) {
        this.managerIds = managerIds;
    }

    @Override
    public List<Long> getAllCompanyIds() {
        return new ArrayList<Long>(emptyIfNull(getCompanyIds()));
    }

    @Override
    public List<Long> getAllPersonIds() {
        return new ArrayList<Long>();
    }

    @Override
    public List<Long> getAllProductIds() {
        return new ArrayList<Long>(emptyIfNull(getProductIds()));
    }

    @Override
    public List<Long> getAllDirectionIds() {
        return new ArrayList<Long>();
    }

    @Override
    public List<Long> getAllTagIds() {
        return new ArrayList<Long>();
    }

    @Override
    public List<Long> getAllRegionIds() {
        return new ArrayList<Long>();
    }

    @Override
    public List<Long> getAllPlatformIds() {
        return new ArrayList<Long>();
    }

    @Override
    public Long getPlanId() {
        return null;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                id != null ||
                StringUtils.isNotBlank(name) ||
                CollectionUtils.isNotEmpty(companyIds) ||
                CollectionUtils.isNotEmpty(productIds) ||
                CollectionUtils.isNotEmpty(managerIds) ||
                deliveryRange != null;
    }

    @Override
    public String toString() {
        return "DeliveryQuery{" +
                "id=" + id +
                ", name=" + name +
                ", companyIds=" + companyIds +
                ", productIds=" + productIds +
                ", managerIds=" + managerIds +
                ", deliveryRange=" + deliveryRange +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryQuery caseQuery = (DeliveryQuery) o;
        return Objects.equals(id, caseQuery.id) &&
                Objects.equals(name, caseQuery.name) &&
                Objects.equals(companyIds, caseQuery.companyIds) &&
                Objects.equals(productIds, caseQuery.productIds) &&
                Objects.equals(managerIds, caseQuery.managerIds) &&
                Objects.equals(deliveryRange, caseQuery.deliveryRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, companyIds, productIds, managerIds, deliveryRange);
    }
}
