package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Сокращенное представление Person
 */
@JdbcEntity(selectSql = "person.* FROM person, JSON_TABLE(person.contactInfo, '$.items[*]' COLUMNS ( a VARCHAR(20) PATH '$.a', t VARCHAR(40) PATH '$.t', v VARCHAR(40) PATH '$.v')) info")
public class EmployeeShortView implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="displayname")
    private String displayName;

    @JdbcColumn(name="birthday")
    private Date birthday;

    @JdbcColumn(name="ipaddress")
    private String ipAddress;

    @JdbcColumn(name="isfired")
    private boolean isFired;

    @JdbcColumn(name="firedate")
    private Date fireDate;

    @JdbcColumn(name = "contactInfo", converterType = ConverterType.JSON)
    private ContactInfo contactInfo;

    @JdbcOneToMany(table = "worker_entry", localColumn = "id", remoteColumn = "personId")
    private List<WorkerEntryShortView> workerEntries;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
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
}
