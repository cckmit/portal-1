package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Запрос по IP адресам
 */
public class ReservedIpQuery extends BaseQuery {

    private List<Long> ownerIds;

    private List<Long> subnetIds;

    private Date reservedFrom;

    private Date reservedTo;

    private Date releasedFrom;

    private Date releasedTo;

    private Date lastActiveFrom;

    private Date lastActiveTo;

    private Boolean allowForReserve;

    public ReservedIpQuery() {
        sortField = En_SortField.ip_address;
        sortDir = En_SortDir.ASC;
    }

    public ReservedIpQuery(String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.subnetIds = new ArrayList<>();
    }

    public ReservedIpQuery(Date reservedFrom, Date reservedTo,
                           Date releasedFrom, Date releasedTo,
                           Date activeFrom, Date activeTo,
                           List<Long> ownerIds, List<Long> subnetIds,
                           String searchString,
                           En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.reservedFrom = reservedFrom;
        this.reservedTo = reservedTo;
        this.releasedFrom = releasedFrom;
        this.releasedTo = releasedTo;
        this.lastActiveFrom = activeFrom;
        this.lastActiveTo = activeTo;
        this.ownerIds = ownerIds;
        this.subnetIds = subnetIds;
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

    public Date getLastActiveFrom() {
        return lastActiveFrom;
    }

    public void setLastActiveFrom(Date lastActiveFrom) {
        this.lastActiveFrom = lastActiveFrom;
    }

    public Date getLastActiveTo() { return lastActiveTo; }

    public void setLastActiveTo(Date lastActiveTo) {
        this.lastActiveTo = lastActiveTo;
    }

    public List<Long> getOwnerIds() { return ownerIds; }

    public void setOwnerIds(List<Long> ownerIds) {
        this.ownerIds = ownerIds;
    }

    public void setOwnerId(Long ownerId) {
        if (ownerId == null) {
            if (ownerIds != null)
                this.ownerIds.clear();
            return;
        }
        this.ownerIds = new ArrayList<>();
        this.ownerIds.add(ownerId);
    }

    public List<Long> getSubnetIds() { return subnetIds; }

    public void setSubnetIds(List<Long> subnetIds) {
        this.subnetIds = subnetIds != null ? subnetIds : new ArrayList<>();
    }

    public void setSubnetId(Long subnetId) {
        if (subnetId == null) {
            this.subnetIds.clear();
            return;
        }
        this.subnetIds = new ArrayList<>();
        this.subnetIds.add(subnetId);
    }

    public void addSubnetId(Long subnetId) {
        this.subnetIds.add(subnetId);
    }

    public Boolean isAllowForReserve() { return allowForReserve; }

    public void setAllowForReserve(Boolean allowForReserve) { this.allowForReserve = allowForReserve; }

    @Override
    public String toString() {
        return "ReservedIpQuery{" +
                "ownerIds=" + ownerIds +
                ", subnetIds=" + subnetIds +
                ", reservedFrom=" + reservedFrom +
                ", reservedTo=" + reservedTo +
                ", releasedFrom=" + releasedFrom +
                ", releasedTo=" + releasedTo +
                ", lastActiveFrom=" + lastActiveFrom +
                ", lastActiveTo=" + lastActiveTo +
                ", allowForReserve=" + allowForReserve +
                '}';
    }
}
