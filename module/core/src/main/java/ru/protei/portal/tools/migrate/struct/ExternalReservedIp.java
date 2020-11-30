package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.LegacyEntity;
import ru.protei.portal.tools.migrate.Const;

import java.util.Date;

@Table(name="IPRES.Tm_IpReserve")
public class ExternalReservedIp implements LegacyEntity {

    @PrimaryKey
    @Column(name = "nID")
    private Long id;

    @Column(name = "dtCreation")
    private Date created;

    @Column(name = "strCreator")
    private String creator = Const.CREATOR_FIELD_VALUE;

    @Column(name = "strIpAddress")
    private String ipAddress;

    @Column(name = "nSubnetID")
    private Long subnetId;

    @Column(name = "nCustomerID")
    private Long customerID;

    @Column(name = "strComment")
    private String comment;

    @Column(name = "dtReserve")
    private Date dtReserve;

    @Column(name = "dtRelease")
    private Date dtRelease;

    @Column(name = "nForLongTime")
    private boolean forLongTime;

    @Column(name = "nExpiredNoteAdmin")
    private boolean expiredNoteAdmin;

    @Column(name = "nExpiredNoteUser")
    private boolean expiredNoteUser;

    public ExternalReservedIp() { }

    @Override
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Date getCreated() { return created; }

    public void setCreated(Date created) { this.created = created; }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }

    public String getIpAddress() { return ipAddress; }

    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Long getSubnetId() { return subnetId; }

    public void setSubnetId(Long subnetId) { this.subnetId = subnetId; }

    public Long getCustomerID() { return customerID; }

    public void setCustomerID(Long customerID) { this.customerID = customerID; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public Date getDtReserve() { return dtReserve; }

    public void setDtReserve(Date dtReserve) { this.dtReserve = dtReserve; }

    public Date getDtRelease() { return dtRelease; }

    public void setDtRelease(Date dtRelease) { this.dtRelease = dtRelease; }

    public boolean isForLongTime() { return forLongTime; }

    public void setForLongTime(boolean forLongTime) { this.forLongTime = forLongTime; }

    public boolean isExpiredNoteAdmin() { return expiredNoteAdmin; }

    public void setExpiredNoteAdmin(boolean expiredNoteAdmin) { this.expiredNoteAdmin = expiredNoteAdmin; }

    public boolean isExpiredNoteUser() { return expiredNoteUser; }

    public void setExpiredNoteUser(boolean expiredNoteUser) { this.expiredNoteUser = expiredNoteUser; }
}
