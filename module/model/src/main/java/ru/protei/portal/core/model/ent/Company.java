package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EntityOptionSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * @author michael
 */
@JdbcEntity(table = "Company")
public class Company extends AuditableObject implements EntityOptionSupport {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcJoinedObject( localColumn = "category_id", table = "company_category" )
    private CompanyCategory category;

    @JdbcColumn(name = "groupId")
    private Long groupId;

    @JdbcJoinedObject(localColumn = "groupId", remoteColumn = "id", updateLocalColumn = false)
    private CompanyGroup companyGroup;

    @JdbcColumn(name = "cname")
    private String cname;

    @JdbcColumn(name = "contactInfo", converterType = ConverterType.JSON)
    private ContactInfo contactInfo;

    @JdbcColumn(name = "info")
    private String info;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcOneToMany(table = "CompanySubscription", localColumn = "id", remoteColumn = "company_id" )
    private List<CompanySubscription> subscriptions;

    public static Company fromEntityOption(EntityOption entityOption){
        if(entityOption == null)
            return null;

        Company company = new Company(entityOption.getId());
        company.setCname(entityOption.getDisplayText());
        return company;
    }

    public Company() {
        this(null);
    }

    public Company(Long id) {
        this.contactInfo = new ContactInfo();
        this.id = id;
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

    @Override
    public int hashCode() {
        return this.id == null ? -1 : this.id.intValue();
    }

    @Override
    public EntityOption toEntityOption() {
        return new EntityOption(this.cname, this.id);
    }

    public void setCompanyGroup( CompanyGroup companyGroup ) {
        this.companyGroup = companyGroup;
    }

    public List< CompanySubscription > getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions( List< CompanySubscription > subscriptions ) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String getAuditType() {
        return "Company";
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", category=" + category +
                ", groupId=" + groupId +
                ", companyGroup=" + companyGroup +
                ", cname='" + cname + '\'' +
                ", contactInfo=" + contactInfo +
                ", info='" + info + '\'' +
                ", created=" + created +
                ", subscriptions=" + subscriptions +
                '}';
    }
}
