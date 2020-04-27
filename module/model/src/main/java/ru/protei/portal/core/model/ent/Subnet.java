package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Подсеть
 */
@JdbcEntity(table = "subnet")
public class Subnet extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTOINCREMENT)
    private Long id;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcColumn(name="creator_id")
    private Long creatorId;

    @JdbcJoinedColumn(mappedColumn = "displayShortName", localColumn = "creator_id", remoteColumn = "id", table = "person")
    private String creator;

    @JdbcColumn(name="address")
    private String address;

    @JdbcColumn(name="mask")
    private String mask;

    @JdbcColumn(name="comment")
    private String comment;

    private Long registeredIPs;

    private Long freeIps;

    public Subnet() {}

    @Override
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Date getCreated() { return created; }

    public void setCreated(Date created) { this.created = created; }

    public Long getCreatorId() { return creatorId; }

    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getMask() { return mask; }

    public void setMask(String mask) { this.mask = mask; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public Long getRegisteredIPs() { return registeredIPs; }

    public void setRegisteredIPs(Long registeredIPs) { this.registeredIPs = registeredIPs; }

    public Long getFreeIps() { return freeIps; }

    public void setFreeIps(Long freeIps) { this.freeIps = freeIps; }

    public EntityOption toEntityOption() {
        EntityOption entityOption = new EntityOption();
        entityOption.setId(getId());
        entityOption.setDisplayText(getAddress() + "0/" + getMask());
        return entityOption;
    }

    @Override
    public String getAuditType() {
        return "Subnet";
    }

    @Override
    public String toString() {
        return "Subnet{" +
                "id=" + id +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", address='" + address + '\'' +
                ", mask='" + mask + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}