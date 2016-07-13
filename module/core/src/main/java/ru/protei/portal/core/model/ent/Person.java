package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;

/**
 * Created by michael on 30.03.16.
 */
@JdbcEntity(table = "Person")
public class Person {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;
    @JdbcColumn(name = "created")
    private Date created;
    @JdbcColumn(name = "creator")
    private String creator;
    @JdbcColumn(name="company_id")
    private Long companyId;

    @JdbcColumn(name = "displayPosition")
    private String position;

    @JdbcColumn(name="firstname")
    private String firstName;
    @JdbcColumn(name="lastname")
    private String lastName;
    @JdbcColumn(name="secondname")
    private String secondName;
    @JdbcColumn(name="displayname")
    private String displayName;

    /**
     * constraint CHK_PERSON_SEX check (SEX in ('M','F','-')),
     */
    @JdbcColumn(name="sex")
    private String sex;

    @JdbcColumn(name="birthday")
    private Date birthday;

    @JdbcColumn(name="ipaddress")
    private String ipAddress;

    @JdbcColumn(name="phone_work")
    private String workPhone;

    @JdbcColumn(name="phone_home")
    private String homePhone;

    @JdbcColumn(name="phone_mobile")
    private String mobilePhone;


    @JdbcColumn(name="email")
    private String email;

    @JdbcColumn(name="email_own")
    private String email_own;


    @JdbcColumn(name="fax")
    private String fax;
    @JdbcColumn(name="fax_home")
    private String faxHome;

    @JdbcColumn(name="address")
    private String address;

    @JdbcColumn(name="address_home")
    private String addressHome;

    @JdbcColumn(name="icq")
    private String icq;

    @JdbcColumn(name="jabber")
    private String jabber;

    @JdbcColumn(name="passportinfo")
    private String passportInfo;

    @JdbcColumn(name="info")
    private String info;

    @JdbcColumn(name="isdeleted")
    private boolean isDeleted;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail_own() {
        return email_own;
    }

    public void setEmail_own(String email_own) {
        this.email_own = email_own;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFaxHome() {
        return faxHome;
    }

    public void setFaxHome(String faxHome) {
        this.faxHome = faxHome;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressHome() {
        return addressHome;
    }

    public void setAddressHome(String addressHome) {
        this.addressHome = addressHome;
    }

    public String getIcq() {
        return icq;
    }

    public void setIcq(String icq) {
        this.icq = icq;
    }

    public String getJabber() {
        return jabber;
    }

    public void setJabber(String jabber) {
        this.jabber = jabber;
    }

    public String getPassportInfo() {
        return passportInfo;
    }

    public void setPassportInfo(String passportInfo) {
        this.passportInfo = passportInfo;
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

    public String toDebugString () {
        return this.getDisplayName();
    }
}
