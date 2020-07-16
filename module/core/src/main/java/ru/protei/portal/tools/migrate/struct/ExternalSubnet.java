package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.LegacyEntity;
import ru.protei.portal.tools.migrate.Const;

import java.util.Date;

@Table(name="IPRES.Tm_IpSubnetReserve")
public class ExternalSubnet implements LegacyEntity {

    @PrimaryKey
    @Column(name = "nID")
    private Long id;

    @Column(name = "dtCreation")
    private Date created;

    @Column(name = "strCreator")
    private String creator = Const.CREATOR_FIELD_VALUE;

    @Column(name = "strSubnetAddress")
    private String subnetAddress;

    @Column(name = "strSubnetMask")
    private String subnetMask;

    @Column(name = "strComment")
    private String comment;

    @Column(name = "nForDepartment")
    private boolean forDepartment;


    public ExternalSubnet() { }

    @Override
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Date getCreated() { return created; }

    public void setCreated(Date created) { this.created = created; }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }

    public String getSubnetAddress() { return subnetAddress; }

    public void setSubnetAddress(String subnetAddress) { this.subnetAddress = subnetAddress; }

    public String getSubnetMask() { return subnetMask; }

    public void setSubnetMask(String subnetMask) { this.subnetMask = subnetMask; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public boolean isForDepartment() { return forDepartment; }

    public void setForDepartment(boolean forDepartment) { this.forDepartment = forDepartment; }
}
