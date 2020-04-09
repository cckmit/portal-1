package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EntityOptionSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author michael
 */
@JdbcEntity(table = "company")
public class Company extends AuditableObject implements EntityOptionSupport {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcJoinedObject( localColumn = "category_id", table = "company_category" )
    private CompanyCategory category;

    @JdbcColumn(name = "groupId")
    private Long groupId;

    @JdbcJoinedObject(localColumn = "groupId", remoteColumn = "id", updateLocalColumn = false)
    private CompanyGroup companyGroup;

    @JdbcColumn(name = "parent_company_id")
    private Long parentCompanyId;

    // winter не поддерживает JdbcJoinedObject на ту же сущность во избежание рекурсии
    private String parentCompanyName;

    @JdbcOneToMany(table = "company", localColumn = "id", remoteColumn = "parent_company_id" )
    private List<Company> childCompanies;

    @JdbcColumn(name = "cname")
    private String cname;

    @JdbcColumn(name = "contactInfo", converterType = ConverterType.JSON)
    private ContactInfo contactInfo;

    @JdbcColumn(name = "info")
    private String info;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "old_id")
    private Long oldId;

    @JdbcColumn(name = "is_hidden")
    private Boolean isHidden;

    @JdbcOneToMany(table = "Company_Subscription", localColumn = "id", remoteColumn = "company_id" )
    private List<CompanySubscription> subscriptions;

    @JdbcManyToMany(linkTable = "case_state_to_company", localLinkColumn = "company_id", remoteLinkColumn = "state_id")
    private List<CaseState> caseStates;

    @JdbcColumn(name = "is_deprecated")
    private boolean isArchived;

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

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
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

    public List<CaseState> getCaseStates() {
        return caseStates;
    }

    public void setCaseStates(List<CaseState> caseStates) {
        this.caseStates = caseStates;
    }

    @Override
    public String getAuditType() {
        return "Company";
    }

    public Long getParentCompanyId() {
        return parentCompanyId;
    }

    public void setParentCompanyId( Long parentCompanyId ) {
        this.parentCompanyId = parentCompanyId;
    }

    public String getParentCompanyName() {
        return parentCompanyName;
    }

    public void setParentCompanyName( String parentCompany ) {
        this.parentCompanyName = parentCompany;
    }

    public List<Company> getChildCompanies() {
        return childCompanies;
    }

    public void setChildCompanies( List<Company> childCompanies ) {
        this.childCompanies = childCompanies;
    }

    public Boolean getHideden() {
        return isHidden;
    }

    public void setHideden(Boolean hideden) {
        isHidden = hideden;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(Boolean deleted) {
        isArchived = deleted;
    }

    public Collection<Long> getCompanyAndChildIds() {
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(getId());
        if (getChildCompanies() != null) {
            ids.addAll(getChildCompanies().stream().map(Company::getId).collect(Collectors.toList()));
        }
        return ids;
    }

    @Override
    public boolean equals(Object obj) {
        if (id != null) {
            return obj instanceof Company && id.equals(((Company) obj).getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", category=" + category +
                ", groupId=" + groupId +
                ", companyGroup=" + companyGroup +
                ", parentCompanyId=" + parentCompanyId +
                ", parentCompanyName='" + parentCompanyName + '\'' +
                ", childCompanies=" + childCompanies +
                ", cname='" + cname + '\'' +
                ", contactInfo=" + contactInfo +
                ", info='" + info + '\'' +
                ", created=" + created +
                ", oldId=" + oldId +
                ", isHidden=" + isHidden +
                ", subscriptions=" + subscriptions +
                ", caseStates=" + caseStates +
                ", isArchived=" + isArchived +
                '}';
    }
}
