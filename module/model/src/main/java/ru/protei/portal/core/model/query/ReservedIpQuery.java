package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    private Date activeFrom;

    private Date activeTo;

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
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
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

    public Date getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Date getActiveTo() { return activeTo; }

    public void setActiveTo(Date activeTo) {
        this.activeTo = activeTo;
    }

    public List<Long> getOwnerIds() {
        return ownerIds;
    }

    public void setOwnerIds(List<Long> ownerIds) {
        this.ownerIds = ownerIds;
    }

    public void setOwnerId(Long ownerId) {
        if (ownerId == null) {
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

/*
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
    }*/

/*    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                CollectionUtils.isNotEmpty(ownerIds) ||
                CollectionUtils.isNotEmpty(subnetIds) ||
                reservedFrom != null ||
                reservedTo != null ||
                releasedFrom != null ||
                releasedTo != null ||
                activeFrom != null ||
                activeTo != null;
    }*/

    @Override
    public String toString() {
        return "ReservedIpQuery{" +
                "ownerIds=" + ownerIds +
                ", subnetIds=" + subnetIds +
                ", reservedFrom=" + reservedFrom +
                ", reservedTo=" + reservedTo +
                ", releasedFrom=" + releasedFrom +
                ", releasedTo=" + releasedTo +
                ", activeFrom=" + activeFrom +
                ", activeTo=" + activeTo +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                '}';
    }
}