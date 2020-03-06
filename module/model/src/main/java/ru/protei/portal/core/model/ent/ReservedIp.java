package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Договор
 */
@JdbcEntity(table = "reserved_ip")
public class ReservedIp extends AuditableObject implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTOINCREMENT)
    private Long id;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcColumn(name="creator_id")
    private Long creatorId;

    @JdbcColumn(name="subnet_id")
    private Long subnetId;

    @JdbcJoinedObject(localColumn = "subnet_id", remoteColumn = "id", table = "subnet")
    private Subnet subnet;

    @JdbcColumn(name="owner_id")
    private Long ownerId;

    @JdbcJoinedObject( localColumn = "owner_id", remoteColumn = "id", table = "person")
    private PersonShortView owner;

    @JdbcColumn(name="ip_address")
    private String ipAddress;

    @JdbcColumn(name="mac_address")
    private String macAddress;

    @JdbcColumn(name="reserve_date")
    private Date reserveDate;

    @JdbcColumn(name = "release_date")
    private Date releaseDate;

    @JdbcColumn(name="comment")
    private String comment;

    @JdbcColumn(name = "last_check_date")
    private Date lastCheckDate;

    @JdbcColumn(name = "last_check_info")
    private String lastCheckInfo;

    @Override
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Date getCreated() { return created; }

    public void setCreated(Date created) { this.created = created; }

    public Long getCreatorId() { return creatorId; }

    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public Long getSubnetId() { return subnetId; }

    public void setSubnetId(Long subnetId) { this.subnetId = subnetId; }

    public Subnet getSubnet() { return subnet; }

    public void setSubnet(Subnet subnet) { this.subnet = subnet; }

    public Long getOwnerId() { return ownerId; }

    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public PersonShortView getOwner() { return owner; }

    public void setOwner(PersonShortView owner) { this.owner = owner; }

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

    public Date getLastCheckDate() { return lastCheckDate; }

    public void setLastCheckDate(Date lastCheckDate) { this.lastCheckDate = lastCheckDate; }

    public String getLastCheckInfo() { return lastCheckInfo; }

    public void setLastCheckInfo(String lastCheckInfo) { this.lastCheckInfo = lastCheckInfo; }

    public EntityOption toEntityOption() {
        EntityOption entityOption = new EntityOption();
        entityOption.setId(getId());
        entityOption.setDisplayText(getIpAddress());
        return entityOption;
    }

    @Override
    public String getAuditType() {
        return "ReservedIp";
    }

    @Override
    public String toString() {
        return "ReservedIp{" +
                "id=" + id +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", subnetId=" + subnetId +
                ", subnet=" + subnet +
                ", ownerId=" + ownerId +
                ", ipAddress='" + ipAddress + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", reserveDate=" + reserveDate +
                ", releaseDate=" + releaseDate +
                ", comment='" + comment + '\'' +
                ", lastCheckDate=" + lastCheckDate +
                ", lastCheckInfo='" + lastCheckInfo + '\'' +
                '}';
    }
}