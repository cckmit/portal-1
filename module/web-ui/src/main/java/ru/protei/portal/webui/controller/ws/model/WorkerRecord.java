package ru.protei.portal.webui.controller.ws.model;

import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.webui.controller.ws.utils.HelperService;

/**
 * Created by turik on 17.08.16.
 */
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

    private Boolean isDeleted;
    private Boolean isFired;

    private Long workerId;
    private Long departmentId;
    private Long positionId;

    private String hireDate;
    private String fireDate;
    private String hireOrderNo;
    private String fireOrderNo;
    private Boolean active;

    private String positionName;

    public WorkerRecord() {
    }

    public WorkerRecord(Person p) {
        copy (p);
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhoneWork() {
        return phoneWork;
    }

    public void setPhoneWork(String phoneWork) {
        this.phoneWork = phoneWork;
    }

    public String getPhoneHome() {
        return phoneHome;
    }

    public void setPhoneHome(String phoneHome) {
        this.phoneHome = phoneHome;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailOwn() {
        return emailOwn;
    }

    public void setEmailOwn(String emailOwn) {
        this.emailOwn = emailOwn;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean isFired() {
        return isFired;
    }

    public void setFired(Boolean isFired) {
        this.isFired = isFired;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    public String getFireDate() {
        return fireDate;
    }

    public void setFireDate(String fireDate) {
        this.fireDate = fireDate;
    }

    public String getHireOrderNo() {
        return hireOrderNo;
    }

    public void setHireOrderNo(String hireOrderNo) {
        this.hireOrderNo = hireOrderNo;
    }

    public String getFireOrderNo() {
        return fireOrderNo;
    }

    public void setFireOrderNo(String fireOrderNo) {
        this.fireOrderNo = fireOrderNo;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

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
        setSex (p.getSex () != null ? p.getSex ().equals ("M") ? 1 : p.getSex ().equals ("F") ? 2 : null : null);
        setBirthday (HelperService.DATE.format (p.getBirthday ()));
        setIpAddress (p.getIpAddress ());
        setPhoneWork (p.getWorkPhone ());
        setPhoneMobile (p.getMobilePhone ());
        setPhoneHome (p.getHomePhone ());
        setPassportInfo (p.getPassportInfo ());
        setInfo (p.getInfo ());
        setAddress (p.getAddress ());
        setAddressHome (p.getAddressHome ());
        setEmail (p.getEmail ());
        setEmailOwn (p.getEmail_own ());
        setFax (p.getFax ());
        setDeleted (p.isDeleted ());
    }

}
