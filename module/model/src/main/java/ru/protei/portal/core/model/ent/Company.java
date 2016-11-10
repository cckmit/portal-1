package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EntityOptionSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author michael
 */
@JdbcEntity(table = "Company")
public class Company implements Serializable,EntityOptionSupport {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcJoinedObject( localColumn = "category_id", table = "company_category" )
    private CompanyCategory category;

    @JdbcColumn(name = "groupId")
    private Long groupId;

    @JdbcJoinedObject(localColumn = "groupId", remoteColumn = "id", updateLocalColumn = false)
    CompanyGroup companyGroup;


    @JdbcColumn(name = "address_dejure")
    private String addressDejure;

    @JdbcColumn(name = "address_fact")
    private String addressFact;

    @JdbcColumn(name = "cname")
    private String cname;

    @JdbcColumn(name = "contactInfo", converterType = ConverterType.JSON)
    private ContactInfo contactInfo;

    @JdbcColumn(name = "info")
    private String info;

    @JdbcColumn(name = "created")
    private Date created;


    public Company() {
        contactInfo = new ContactInfo();
    }

    public String getAddressDejure() {
        return this.addressDejure;
    }

    public String getAddressFact() {
        return this.addressFact;
    }

    public String getCname() {
        return this.cname;
    }


    public Long getId() {
        return this.id;
    }

    public String getInfo() {
        return this.info;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setAddressDejure(String addressDejure) {
        this.addressDejure = addressDejure;
    }

    public void setAddressFact(String addressFact) {
        this.addressFact = addressFact;
    }

    public Long getCategoryId() {
        return category == null ? null : category.getId();
    }

    public CompanyCategory getCategory() {
        return category;
    }

    public void setCategory( CompanyCategory category ) {
        this.category = category;
    }

    public void setCname( String cname) {
        this.cname = cname;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public CompanyGroup getCompanyGroup() {
        return companyGroup;
    }

    public void setCompanyGroup(CompanyGroup companyGroup) {
        this.companyGroup = companyGroup;
        this.groupId = companyGroup.getId();
    }

    @Override
    public int hashCode() {
        return this.id == null ? -1 : this.id.intValue();
    }


    @Override
    public EntityOption toEntityOption() {
        return new EntityOption(this.cname, this.id);
    }

    @Override
    public String toString() {
        return new StringBuilder("Company{")
                .append("id=").append(id)
                .append(", categoryId=").append(getCategoryId())
                .append(", groupId=").append(groupId)
                .append(", addressDejure='").append(addressDejure).append('\'')
                .append(", addressFact='").append(addressFact).append('\'')
                .append(", cname='").append(cname).append('\'')
                .append(", info='").append(info).append('\'')
                .append(", created=").append(created)
                .append(", group=").append(getCompanyGroup())
                .append('}').toString();
    }
}
