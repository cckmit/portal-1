package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EntityOptionSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Договор
 */
@JdbcEntity(table = "subnet")
public class Subnet extends AuditableObject implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTOINCREMENT)
    private Long id;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcColumn(name="creator_id")
    private Long creatorId;

    @JdbcJoinedColumn(mappedColumn = "displayShortName", localColumn = "owner_id", remoteColumn = "id", table = "person")
    private String creator;

    @JdbcColumn(name="address")
    private String address;

    @JdbcColumn(name="mask")
    private String mask;

    @JdbcColumn(name = "is_local")
    private boolean isLocal;

    @JdbcColumn(name="comment")
    private String comment;

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

    public boolean isLocal() { return isLocal; }

    public void setLocal(boolean local) { isLocal = local; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

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
                ", isLocal=" + isLocal +
                ", comment='" + comment + '\'' +
                '}';
    }
}