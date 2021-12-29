package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@JdbcEntity(table = "person")
public class Person extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "creator")
    private String creator;

    @JdbcColumn(name="company_id")
    private Long companyId;

    @JdbcJoinedObject (localColumn = "company_id", remoteColumn = "id" )
    private Company company;

    @JdbcColumn(name = "displayPosition")
    private String position;

    @JdbcColumn(name = "department")
    private String department;

    @JdbcColumn(name="firstname")
    private String firstName;

    @JdbcColumn(name="lastname")
    private String lastName;

    @JdbcColumn(name="secondname")
    private String secondName;

    @JdbcColumn(name=Columns.DISPLAY_NAME)
    private String displayName;

    @JdbcColumn(name=Columns.DISPLAY_SHORT_NAME)
    private String displayShortName;

    /**
     * constraint CHK_PERSON_SEX check (SEX in ('M','F','-')),
     */
    @JdbcColumn(name="sex")
    private String genderCode;

    @JdbcColumn(name="birthday")
    private Date birthday;

    @JdbcColumn(name="ipaddress")
    private String ipAddress;

    @JdbcColumn(name="info")
    private String info;

    @JdbcColumn(name="isdeleted")
    private boolean isDeleted;

    @JdbcColumn(name = Columns.IS_FIRED)
    private boolean isFired;

    @JdbcColumn(name = Columns.FIRE_DATE)
    private Date fireDate;

    @JdbcManyToMany(linkTable = "contact_item_person", localLinkColumn = "person_id", remoteLinkColumn = "contact_item_id")
    private List<ContactItem> contactItems;

    @JdbcColumn(name = "old_id")
    private Long oldId;

    @JdbcColumn(name = "relations")
    private String relations;

    @JdbcColumn(name = "locale")
    private String locale;

    private List<String> logins;

    public static Person fromPersonShortView( PersonShortView personShortView ){
        if(personShortView == null)
            return null;

        Person person = new Person();
        person.setId( personShortView.getId());
        person.setDisplayShortName( personShortView.getName());
        person.setFired( personShortView.isFired());
        return person;
    }

    public static Person fromPersonFullNameShortView(PersonShortView personShortView) {
        if(personShortView == null)
            return null;

        Person person = new Person();
        person.setId( personShortView.getId());
        person.setDisplayName( personShortView.getName());
        person.setFired( personShortView.isFired());
        return person;
    }

    public Person(Long personId){
        this();
        this.id = personId;
    }

    public Person(Long personId, Long companyId){
        this();
        this.id = personId;
        this.companyId = companyId;
    }

    public Person () {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
        /** on deserialization (called by jackson parser) it will be null **/
        if (company != null)
            this.companyId = company.getId();
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }


    public String getDepartment(){return  department; }

    public void setDepartment(String department){
        this.department = department;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    public List<ContactItem> getContactItems() {
        if (contactItems == null) {
            contactItems = new ArrayList<>();
        }
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isFired() {
        return isFired;
    }

    public void setFired(boolean isFired, Date fireDate) {
        this.isFired = isFired;
        this.fireDate = fireDate;
    }

    public void setFired(Date fireDate) {
        this.isFired = true;
        this.fireDate = fireDate;
    }

    public Date getFireDate() {
        return fireDate;
    }

    public void setFired(boolean isFired) {
        this.isFired = isFired;
    }

    public String toDebugString () {
        return this.getDisplayName();
    }

    public String getDisplayShortName() {
        return displayShortName;
    }

    public void setDisplayShortName(String displayShortName) {
        this.displayShortName = displayShortName;
    }

    public En_Gender getGender () {
        return En_Gender.parse(this.genderCode);
    }

    public void setGender (En_Gender gender) {
        this.genderCode = gender.getCode();
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String getAuditType() {
        return "Person";
    }

    @Override
    public int hashCode() {
        return Objects.hash( id );
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals( id, person.id );
    }

    public String getRelations() {
        return relations;
    }

    public void setRelations(String relations) {
        this.relations = relations;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public void resetPrivacyInfo() {
        department = null;
        position = null;
        info = null;
        ipAddress = null;

        if (contactItems != null) {
            contactItems.removeIf((info) -> !info.isItemOf(En_ContactItemType.EMAIL));
        }
    }

    public List<String> getLogins() {
        return logins;
    }

    public void setLogins(List<String> logins) {
        this.logins = logins;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", created=" + created +
                ", creator='" + creator + '\'' +
                ", companyId=" + companyId +
                ", company=" + company +
                ", position='" + position + '\'' +
                ", department='" + department + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", displayShortName='" + displayShortName + '\'' +
                ", genderCode='" + genderCode + '\'' +
                ", birthday=" + birthday +
                ", ipAddress='" + ipAddress + '\'' +
                ", info='" + info + '\'' +
                ", isDeleted=" + isDeleted +
                ", isFired=" + isFired +
                ", fireDate=" + fireDate +
                ", contactItems=" + contactItems +
                ", oldId=" + oldId +
                ", relations='" + relations + '\'' +
                ", locale='" + locale + '\'' +
                ", logins='" + logins + '\'' +
                '}';
    }

    public interface Fields {
        String CONTACT_ITEMS = "contactItems";
    }

    public interface Columns {
        String CONTACT_ITEMS = "contactItems";
        String DISPLAY_SHORT_NAME = "displayShortName";
        String DISPLAY_NAME = "displayname";
        String IS_FIRED = "isfired";
        String FIRE_DATE = "firedate";
    }
}
