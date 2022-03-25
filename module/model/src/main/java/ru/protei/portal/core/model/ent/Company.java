package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EntityOptionSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

@JdbcEntity(table = "company")
public class Company extends AuditableObject implements EntityOptionSupport {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn( name = "category_id" )
    @JdbcEnumerated( EnumType.ID )
    private En_CompanyCategory category;

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

    @JdbcManyToMany(linkTable = "contact_item_company", localLinkColumn = "company_id", remoteLinkColumn = "contact_item_id")
    private List<ContactItem> contactItems;

    @JdbcColumn(name = "info")
    private String info;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "old_id")
    private Long oldId;

    @JdbcColumn(name = "is_hidden")
    private Boolean isHidden;

    @JdbcOneToMany(table = "company_subscription", localColumn = "id", remoteColumn = "company_id" )
    private List<CompanySubscription> subscriptions;

    @JdbcManyToMany(linkTable = "case_state_to_company", localLinkColumn = "company_id", remoteLinkColumn = "state_id")
    private List<CaseState> caseStates;

    @JdbcColumn(name = "is_deprecated")
    private boolean isArchived;

    @JdbcColumn(name = "auto_open_issue")
    private Boolean autoOpenIssue;

    @JdbcOneToMany(table = "common_manager", localColumn = "id", remoteColumn = "company_id" )
    private List<CommonManager> commonManagerList;

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

    public En_CompanyCategory getCategory() {
        return category;
    }

    public void setCategory( En_CompanyCategory category ) {
        this.category = category;
    }

    public Integer getCategoryId() {
        return category != null ? category.getId() : null;
    }

    public void setCategoryId(Integer categoryId) {
        this.category = En_CompanyCategory.findById(categoryId);
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

    public List<ContactItem> getContactItems() {
        return contactItems;
    }

    public ContactInfo getContactInfo() {
        if (contactItems == null) {
            contactItems = new ArrayList<>();
        }
        return new ContactInfo(contactItems);
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactItems = contactInfo != null ? contactInfo.getItems() : null;
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

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(Boolean deleted) {
        isArchived = deleted;
    }

    public Collection<Long> getCompanyAndChildIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(getId());
        ids.addAll(stream(getChildCompanies())
                .filter(company -> !company.isArchived)
                .map(Company::getId)
                .collect(Collectors.toList()));
        return ids;
    }

    public Boolean getAutoOpenIssue() {
        return autoOpenIssue;
    }

    public void setAutoOpenIssue(Boolean autoOpenIssue) {
        this.autoOpenIssue = autoOpenIssue;
    }

    public List<CommonManager> getCommonManagerList() {
        return commonManagerList;
    }

    public void setCommonManagerList(List<CommonManager> commonManagerList) {
        this.commonManagerList = commonManagerList;
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
                ", contactItems=" + contactItems +
                ", info='" + info + '\'' +
                ", created=" + created +
                ", oldId=" + oldId +
                ", isHidden=" + isHidden +
                ", subscriptions=" + subscriptions +
                ", caseStates=" + caseStates +
                ", isArchived=" + isArchived +
                ", autoOpenIssue=" + autoOpenIssue +
                ", commonManagerList=" + commonManagerList +
                '}';
    }

    public interface Fields {
        String CONTACT_ITEMS = "contactItems";
        String COMMON_MANAGER_LIST = "commonManagerList";
    }
}
