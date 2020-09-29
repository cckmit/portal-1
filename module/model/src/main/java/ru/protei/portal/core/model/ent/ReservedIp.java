package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;

/**
 * Зарезервированный IP-адрес
 */
@JdbcEntity(table = "reserved_ip")
public class ReservedIp extends AuditableObject {

    public static final String AUDIT_TYPE = "ReservedIp";

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTOINCREMENT)
    private Long id;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcColumn(name="creator_id")
    private Long creatorId;

    @JdbcJoinedColumn(mappedColumn = "displayShortName", localColumn = "creator_id", remoteColumn = "id", table = "person")
    private String creator;

    @JdbcColumn(name="subnet_id")
    private Long subnetId;

    @JdbcJoinedObject( localColumn = "subnet_id", remoteColumn = "id", table = "subnet", updateLocalColumn = false )
    private Subnet subnet;

    @JdbcColumn(name="owner_id")
    private Long ownerId;

    @JdbcJoinedColumn( localColumn = "owner_id", table = "person", remoteColumn = "id", mappedColumn = "displayName")
    private String ownerName;

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

    public ReservedIp() {}

    @Override
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Date getCreated() { return created; }

    public void setCreated(Date created) { this.created = created; }

    public Long getCreatorId() { return creatorId; }

    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }

    public Long getSubnetId() { return subnetId; }

    public void setSubnetId(Long subnetId) { this.subnetId = subnetId; }

    public Subnet getSubnet() { return subnet; }

    public void setSubnet(Subnet subnet) { this.subnet = subnet; }

    public Long getOwnerId() { return ownerId; }

    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }

    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

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

    public static ReservedIp createByTemplate(ReservedIp templ) {
        ReservedIp reservedIp = new ReservedIp();
        reservedIp.setCreated(templ.created);
        reservedIp.setCreatorId(templ.creatorId);
        reservedIp.setOwnerId(templ.getOwnerId());
        reservedIp.setReserveDate(templ.reserveDate);
        reservedIp.setReleaseDate(templ.releaseDate);
        reservedIp.setComment(templ.comment);
        return reservedIp;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
    public String toString() {
        return "ReservedIp{" +
                "id=" + id +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", creator='" + creator + '\'' +
                ", subnetId=" + subnetId +
                ", subnet=" + subnet +
                ", ownerId=" + ownerId +
                ", ownerName='" + ownerName + '\'' +
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
