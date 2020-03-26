package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.view.SubnetOption;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Объект-wrapper для запроса резервирования IP адресов по параметрам
 */
public class ReservedIpRequest implements Serializable {

    private Set<SubnetOption> subnets;

    private Long ownerId;

    private Long number;

    private boolean exact;

    private String ipAddress;

    private String macAddress;

    private Date reserveDate;

    private Date releaseDate;

    private String comment;

    public ReservedIpRequest() {}

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

    public Date getReserveDate() { return reserveDate; }

    public void setReserveDate(Date reserveDate) { this.reserveDate = reserveDate; }

    public Date getReleaseDate() { return releaseDate; }

    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }
}