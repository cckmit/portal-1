package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.SubnetOption;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Объект-wrapper для запроса резервирования IP адресов по параметрам
 */
public class ReservedIpRequest extends AuditableObject {

    public static final String AUDIT_TYPE = "ReservedIpRequest";

    private Set<SubnetOption> subnets;

    private Long ownerId;

    private Long number;

    private boolean exact;

    private String ipAddress;

    private String macAddress;

    private En_DateIntervalType dateIntervalType;

    private Date reserveDate;

    private Date releaseDate;

    private String comment;

    public ReservedIpRequest() {}

    @Override
    public Long getId() { return null; }

    public Set<SubnetOption> getSubnets() { return subnets; }

    public void setSubnets(Set<SubnetOption> subnets) { this.subnets = subnets; }

    public Long getOwnerId() { return ownerId; }

    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Long getNumber() { return number; }

    public void setNumber(Long number) { this.number = number; }

    public boolean isExact() { return exact; }

    public void setExact(boolean exact) { this.exact = exact; }

    public String getIpAddress() { return ipAddress; }

    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getMacAddress() { return macAddress; }

    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public En_DateIntervalType getDateIntervalType() { return dateIntervalType; }

    public void setDateIntervalType(En_DateIntervalType dateIntervalType) {
        this.dateIntervalType = dateIntervalType;
    }

    public Date getReserveDate() { return reserveDate; }

    public void setReserveDate(Date reserveDate) { this.reserveDate = reserveDate; }

    public Date getReleaseDate() { return releaseDate; }

    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public String getSubnetAddress () {
        if (StringUtils.isEmpty(ipAddress))
            return null;

        return ipAddress.substring(0, ipAddress.lastIndexOf("."));
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
    public String toString() {
        return "ReservedIpRequest{" +
                "exact=" + exact +
                ", subnets=" + subnets +
                ", ownerId=" + ownerId +
                ", number=" + number +
                ", ipAddress='" + ipAddress + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", dateIntervalType=" + dateIntervalType +
                ", reserveDate=" + reserveDate +
                ", releaseDate=" + releaseDate +
                ", comment='" + comment + '\'' +
                '}';
    }
}