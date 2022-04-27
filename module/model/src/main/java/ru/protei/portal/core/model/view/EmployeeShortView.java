package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Сокращенное представление Person
 */
@JdbcEntity(table = "person")
public class EmployeeShortView implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="company_id")
    private Long companyId;

    @JdbcColumn(name="displayname")
    private String displayName;

    @JdbcColumn(name="displayShortName")
    private String displayShortName;

    @JdbcColumn(name="firstname")
    private String firstName;

    @JdbcColumn(name="lastname")
    private String lastName;

    @JdbcColumn(name="secondname")
    private String secondName;

    @JdbcColumn(name="birthday")
    private Date birthday;

    @JdbcColumn(name="ipaddress")
    private String ipAddress;

    @JdbcColumn(name = "inn")
    private String inn;

    @JdbcColumn(name="isfired")
    private boolean isFired;

    @JdbcColumn(name="firedate")
    private Date fireDate;

    @JdbcColumn(name="sex")
    private String gender;

    @JdbcManyToMany(linkTable = "contact_item_person", localLinkColumn = "person_id", remoteLinkColumn = "contact_item_id")
    private List<ContactItem> contactItems;

    @JdbcOneToMany(table = "worker_entry", localColumn = "id", remoteColumn = "personId")
    private List<WorkerEntryShortView> workerEntries;

    @JdbcEmbed
    private Integer timezoneOffset;

    private List<String> logins;

    private PersonAbsence currentAbsence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayShortName() {
        return displayShortName;
    }

    public void setDisplayShortName(String displayShortName) {
        this.displayShortName = displayShortName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
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

    public List<WorkerEntryShortView> getWorkerEntries() {
        return workerEntries;
    }

    public void setWorkerEntries(List<WorkerEntryShortView> workerEntries) {
        this.workerEntries = workerEntries;
    }

    public boolean isFired() {
        return isFired;
    }

    public void setFired(boolean fired) {
        isFired = fired;
    }

    public Date getFireDate() {
        return fireDate;
    }

    public void setFireDate(Date fireDate) {
        this.fireDate = fireDate;
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

    public En_Gender getGender () {
        return En_Gender.parse(this.gender);
    }

    public void setGender (En_Gender gender) {
        this.gender = gender.getCode();
    }

    public PersonAbsence getCurrentAbsence() {
        return currentAbsence;
    }

    public void setCurrentAbsence(PersonAbsence absence) {
        this.currentAbsence = absence;
    }

    public List<String> getLogins() {
        return logins;
    }

    public void setLogins(List<String> logins) {
        this.logins = logins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeShortView that = (EmployeeShortView) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public interface Fields {
        String CONTACT_ITEMS = "contactItems";
        String WORKER_ENTRIES = "workerEntries";
        String LOGINS = "logins";
    }
}
