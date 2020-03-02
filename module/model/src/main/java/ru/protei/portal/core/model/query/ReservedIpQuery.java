package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.Date;
import java.util.Set;

/**
 * Запрос по IP адресам
 */
public class ReservedIpQuery extends BaseQuery {

    private Set<Long> ownerIds;

    private Boolean onlyLocal;

    private Date reservedFrom;

    private Date reservedTo;

    private Date releasedFrom;

    private Date releasedTo;

    public ReservedIpQuery() {
        sortField = En_SortField.ip_address;
        sortDir = En_SortDir.ASC;
    }

    public ReservedIpQuery(String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
    }

    public ReservedIpQuery(Date reservedFrom, Date reservedTo,
                           Date releasedFrom, Date releasedTo,
                           Set<Long> ownerIds, boolean onlyLocal,
                           String searchString,
                           En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.reservedFrom = reservedFrom;
        this.reservedTo = reservedTo;
        this.releasedFrom = releasedFrom;
        this.releasedTo = releasedTo;
        this.onlyLocal = onlyLocal;
        this.ownerIds = ownerIds;
    }

    public Boolean getOnlyLocal() {
        return onlyLocal;
    }

    public void setOnlyLocal(Boolean onlyLocal) {
        this.onlyLocal = onlyLocal;
    }

    public Date getReservedFrom() {
        return reservedFrom;
    }

    public void setReservedFrom(Date reservedFrom) {
        this.reservedFrom = reservedFrom;
    }

    public Date getReservedTo() {
        return reservedTo;
    }

    public void setReservedTo(Date reservedTo) {
        this.reservedTo = reservedTo;
    }

    public Date getReleasedFrom() {
        return releasedFrom;
    }

    public void setReleasedFrom(Date releasedFrom) {
        this.releasedFrom = releasedFrom;
    }

    public Date getReleasedTo() {
        return releasedTo;
    }

    public void setReleasedTo(Date releasedTo) {
        this.releasedTo = releasedTo;
    }

    public Set<Long> getOwnerIds() {
        return ownerIds;
    }

    public void setOwnerIds(Set<Long> ownerIds) {
        this.ownerIds = ownerIds;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                CollectionUtils.isNotEmpty(ownerIds) ||
                reservedFrom != null ||
                reservedTo != null ||
                releasedFrom != null ||
                releasedTo != null;
    }

    @Override
    public String toString() {
        return "ReservedIpQuery{" +
                "ownerIds=" + ownerIds +
                ", onlyLocal=" + onlyLocal +
                ", reservedFrom=" + reservedFrom +
                ", reservedTo=" + reservedTo +
                ", releasedFrom=" + releasedFrom +
                ", releasedTo=" + releasedTo +
                '}';
    }
}
