package ru.protei.portal.api.model;

import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.tools.migrate.HelperService;
import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by turik on 17.08.16.
 */
@XmlRootElement(name = "worker")
public class WorkerRecord {

    private String companyCode;

    private Long id;
    private String firstName;
    private String lastName;
    private String secondName;
    private Integer sex;
    private String birthday;
    private String phoneWork;
    private String phoneHome;
    private String phoneMobile;
    private String email;
    private String emailOwn;
    private String fax;
    private String address;
    private String addressHome;
    private String passportInfo;
    private String info;
    private String ipAddress;

    private boolean isDeleted;
    private boolean isFired;
    private String fireDate;

    private String workerId;
    private String departmentId;
    private Long registrationId;

    private String hireDate;
    private String hireOrderNo;
    private int active;

    private String positionName;

    public WorkerRecord() {}

    public WorkerRecord(Person p) {
        copy (p);
    }

    public WorkerRecord(WorkerEntry w, EmployeeRegistration r) {
        copy (w);
        setRegistrationId(r == null ? null : r.getId());
    }

    @XmlElement(name = "company-code")
    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @XmlElement(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(name = "first-name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlElement(name = "last-name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @XmlElement(name = "second-name")
    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    @XmlElement(name = "gender")
    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    @XmlElement(name = "birthday")
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @XmlElement(name = "phone-work")
    public String getPhoneWork() {
        return phoneWork;
    }

    public void setPhoneWork(String phoneWork) {
        this.phoneWork = phoneWork;
    }

    @XmlElement(name = "phone-home")
    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    @XmlElement(name = "phone-mobile")
    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    @XmlElement(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement(name = "email-own")
    public String getEmailOwn() {
        return emailOwn;
    }

    public void setEmailOwn(String emailOwn) {
        this.emailOwn = emailOwn;
    }

    @XmlElement(name = "fax")
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @XmlElement(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlElement(name = "address-home")
    public String getAddressHome() {
        return addressHome;
    }

    public void setAddressHome(String addressHome) {
        this.addressHome = addressHome;
    }

    @XmlElement(name = "passport-info")
    public String getPassportInfo() {
        return passportInfo;
    }

    public void setPassportInfo(String passportInfo) {
        this.passportInfo = passportInfo;
    }

    @XmlElement(name = "info")
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @XmlElement(name = "ip")
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @XmlElement(name = "deleted")
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isFired() {
        return isFired;
    }

    public void setFired(boolean isFired) {
        this.isFired = isFired;
    }

    @XmlElement(name = "fire-date")
    public String getFireDate() { return fireDate; }


    public void setFireDate(String fireDate) {
        this.fireDate = fireDate;
        this.isFired = fireDate != null && !fireDate.isEmpty();
    }

    @XmlElement(name = "worker-id")
    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    @XmlElement(name = "department-id")
    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    @XmlElement(name = "registration-id")
    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId( Long registrationId ) {
        this.registrationId = registrationId;
    }

    @XmlElement(name = "hire-date")
    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    @XmlElement(name = "hire-order")
    public String getHireOrderNo() {
        return hireOrderNo;
    }

    public void setHireOrderNo(String hireOrderNo) {
        this.hireOrderNo = hireOrderNo;
    }

    @XmlElement(name = "active")
    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    @XmlElement(name = "position-name")
    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public void copy (Person p) {
        setId (p.getId ());
        setFirstName (p.getFirstName ());
        setLastName (p.getLastName ());
        setSecondName (p.getSecondName ());
        setSex (p.getGender() != null ? p.getGender().equals (En_Gender.MALE) ? new Integer (1) : p.getGender().equals (En_Gender.FEMALE) ? new Integer (2) : null : null);
        setBirthday (p.getBirthday () != null ? HelperService.DATE.format (p.getBirthday ()) : null);
        setIpAddress (p.getIpAddress ());
        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(p.getContactInfo());
        setPhoneWork (contactInfoFacade.getWorkPhone ());
        setPhoneMobile (contactInfoFacade.getMobilePhone ());
        setPhoneHome (contactInfoFacade.getHomePhone ());
        setPassportInfo (p.getPassportInfo ());
        setInfo (p.getInfo ());
        setAddress (contactInfoFacade.getLegalAddress());
        setAddressHome (contactInfoFacade.getHomeAddress());
        setEmail (contactInfoFacade.getEmail ());
        setEmailOwn (contactInfoFacade.getEmail_own ());
        setFax (contactInfoFacade.getFax ());
        setFireDate(p.getFireDate() == null ? null : HelperService.DATE.format(p.getFireDate()));
        setFired(p.isFired());
        setDeleted (p.isDeleted ());
    }

    public void copy (WorkerEntry w) {
        setCompanyCode(w.getExternalCode());
        copy(w.getPerson());
        setWorkerId(w.getExternalId());
        setDepartmentId(w.getDepartmentExternalId());
        setPositionName(w.getPositionName());
        setHireDate(w.getHireDate() == null ? null : HelperService.DATE.format (w.getHireDate()));
        setHireOrderNo(w.getHireOrderNo());
        setActive(w.getActiveFlag());
    }

    @Override
    public String toString() {
        return "WorkerRecord{" +
                "companyCode='" + companyCode + '\'' +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", sex=" + sex +
                ", birthday='" + birthday + '\'' +
                ", phoneWork='" + phoneWork + '\'' +
                ", phoneHome='" + phoneHome + '\'' +
                ", phoneMobile='" + phoneMobile + '\'' +
                ", email='" + email + '\'' +
                ", emailOwn='" + emailOwn + '\'' +
                ", fax='" + fax + '\'' +
                ", address='" + address + '\'' +
                ", addressHome='" + addressHome + '\'' +
                ", passportInfo='" + passportInfo + '\'' +
                ", info='" + info + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", isDeleted=" + isDeleted +
                ", isFired=" + isFired +
                ", fireDate='" + fireDate + '\'' +
                ", workerId='" + workerId + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", registrationId=" + registrationId +
                ", hireDate='" + hireDate + '\'' +
                ", hireOrderNo='" + hireOrderNo + '\'' +
                ", active=" + active +
                ", positionName='" + positionName + '\'' +
                '}';
    }
}
