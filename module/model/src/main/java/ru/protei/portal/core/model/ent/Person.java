package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PersonShortViewSupport;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Objects;

/**
 * Created by michael on 30.03.16.
 */
@JdbcEntity(table = "Person")
public class Person extends AuditableObject implements PersonShortViewSupport, Removable {
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

    @JdbcColumn(name="displayname")
    private String displayName;

    @JdbcColumn(name="displayShortName")
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

//    @JdbcColumn(name="address")
//    private String address;
//
//    @JdbcColumn(name="address_home")
//    private String addressHome;

    @JdbcColumn(name="passportinfo")
    private String passportInfo;

    @JdbcColumn(name="info")
    private String info;

    @JdbcColumn(name="isdeleted")
    private boolean isDeleted;

    @JdbcColumn(name = "isfired")
    private boolean isFired;

    @JdbcColumn(name = "contactInfo", converterType = ConverterType.JSON)
    private ContactInfo contactInfo;

    @JdbcColumn(name = "old_id")
    private Long oldId;

/*
    @JdbcColumn(name = "updated")
    private Date updated;
*/

    @JdbcColumn(name = "relations")
    private String relations;

    public static Person fromPersonShortView( PersonShortView personShortView ){
        if(personShortView == null)
            return null;

        Person person = new Person();
        person.setId( personShortView.getId());
        person.setDisplayShortName( personShortView.getDisplayShortName());
        person.setFired( personShortView.isFired());
        return person;
    }

    public Person(Long personId){
        this();
        this.id = personId;
    }

    public Person () {
        this.contactInfo = new ContactInfo();
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


    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }



//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getAddressHome() {
//        return addressHome;
//    }
//
//    public void setAddressHome(String addressHome) {
//        this.addressHome = addressHome;
//    }

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

    public boolean isFired() {
        return isFired;
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

    @Override
    public String getAuditType() {
        return "Person";
    }

/*
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
*/

    @Override
    public PersonShortView toShortNameShortView() {
        return new PersonShortView(this.displayShortName, this.getId(), this.isFired);
    }

    @Override
    public PersonShortView toFullNameShortView() {
        return new PersonShortView(this.displayName, this.getId(), this.isFired);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Person && Objects.equals(id, ((Person)obj).getId());
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

    @Override
    public boolean isAllowedRemove() {
        return !isDeleted;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", created=" + created +
                ", creator='" + creator + '\"' +
                ", companyId=" + companyId +
                ", company=" + company +
                ", position='" + position + '\"' +
                ", department='" + department + '\"' +
                ", firstName='" + firstName + '\"' +
                ", lastName='" + lastName + '\"' +
                ", secondName='" + secondName + '\"' +
                ", displayName='" + displayName + '\"' +
                ", displayShortName='" + displayShortName + '\"' +
                ", genderCode='" + genderCode + '\"' +
                ", birthday=" + birthday +
                ", ipAddress='" + ipAddress + '\"' +
                ", passportInfo='" + passportInfo + '\"' +
                ", info='" + info + '\"' +
                ", isDeleted=" + isDeleted +
                ", isFired=" + isFired +
                ", contactInfo=" + contactInfo +
                ", relations='" + relations + '\"' +
                ", oldId=" + String.valueOf(oldId) +
                '}';
    }
}
