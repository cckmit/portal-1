package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.LegacyEntity;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.tools.migrate.Const;
import ru.protei.portal.tools.migrate.HelperService;

import java.util.Date;

/**
 * Created by turik on 08.09.16.
 */
@Table(name="\"Resource\".Tm_Person")
public class ExternalPerson implements LegacyEntity {

    private Long id;
    private Date created;
    private String creator = Const.CREATOR_FIELD_VALUE;
    private String client = Const.CLIENT_FIELD_VALUE;
    private String clientIP = Const.CREATOR_HOST_VALUE;
    private Long companyId;
    private String firstName;
    private String lastName;
    private String secondName;
    private String passportInfo;
    private String info;
    private Date birthday;
    private Integer sex;
    private Boolean isDeleted;

    private String department;
    private String position;

    public ExternalPerson() {}

    public ExternalPerson (Person person, String host) {
        this (person, person.getDepartment(), person.getPosition(), host);
    }

    public ExternalPerson(Person person, String departmentName, String positionName, String host) {

        setId (person.getOldId());
        setCreated(person.getCreated());
        setCreator (Const.CREATOR_FIELD_VALUE);
        setClient (Const.CLIENT_FIELD_VALUE);
        setClientIP (host);
        setCompanyId (new Long (1));
        setFirstName (person.getFirstName ());
        setLastName (person.getLastName ());
        setSecondName (person.getSecondName () == null ? "" : person.getSecondName ());
        setPassportInfo (person.getPassportInfo () == null ? "" : person.getPassportInfo ());
        setInfo (person.getInfo () == null ? "" : person.getInfo ());
        setBirthday (person.getBirthday ());
        setSex (person.getGender() != null ? person.getGender().getLegacyId() : null);
        setDeleted (person.isDeleted ());
        setDepartment(departmentName);
        setPosition(positionName);
    }

    public ExternalPerson updateContactFrom(Person person) {
        setFirstName (person.getFirstName ());
        setLastName (person.getLastName ());
        setSecondName (person.getSecondName () == null ? "" : person.getSecondName ());
        setInfo (person.getInfo () == null ? "" : person.getInfo ());
        setBirthday (person.getBirthday ());
        setSex (person.getGender() != null ? person.getGender().getLegacyId() : null);
        setDeleted (person.isDeleted ());
        setDepartment(person.getDepartment());
        setPosition(person.getPosition());
        return this;
    }

    public String getDisplayName () {
        return HelperService.generateDisplayName(this);
    }

    @Column(name = "dtCreation")
    public Date getCreated() {
        return created;
    }

    @Column(name = "dtCreation")
    public void setCreated(Date created) {
        this.created = created;
    }

    @PrimaryKey
    @Column(name="nID")
    public Long getId() {
        return id;
    }

    @PrimaryKey
    @Column(name="nID")
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="nCompanyID")
    public Long getCompanyId() {
        return companyId;
    }

    @Column(name="nCompanyID")
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Column(name="strFirstName")
    public String getFirstName() {
        return firstName;
    }

    @Column(name="strFirstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name="strLastName")
    public String getLastName() {
        return lastName;
    }

    @Column(name="strLastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name="strPatronymic")
    public String getSecondName() {
        return secondName;
    }

    @Column(name="strPatronymic")
    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    @Column(name="strPassportInfo")
    public String getPassportInfo() {
        return passportInfo;
    }

    @Column(name="strPassportInfo")
    public void setPassportInfo(String passportInfo) {
        this.passportInfo = passportInfo;
    }

    @Column(name="strInfo")
    public String getInfo() {
        return info;
    }

    @Column(name="strInfo")
    public void setInfo(String info) {
        this.info = info;
    }

    @Column(name="dtBirthday")
    public Date getBirthday() {
        return birthday;
    }

    @Column(name="dtBirthday")
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Column(name="nSexID")
    public Integer getSex() {
        return sex;
    }

    @Column(name="nSexID")
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    @Column(name="lDeleted")
    public Boolean isDeleted() {
        return isDeleted;
    }

    @Column(name="lDeleted")
    public void setDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Column(name="strCreator")
    public String getCreator() {
        return creator;
    }

    @Column(name="strCreator")
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name="strClient")
    public String getClient() {
        return client;
    }

    @Column(name="strClient")
    public void setClient(String client) {
        this.client = client;
    }

    @Column(name="strClientIP")
    public String getClientIP() {
        return clientIP;
    }

    @Column(name="strClientIP")
    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    @Column(name="strDepartment")
    public String getDepartment() {
        return department;
    }

    @Column(name="strDepartment")
    public void setDepartment(String department) {
        this.department = department;
    }

    @Column(name="strPosition")
    public String getPosition() {
        return position;
    }

    @Column(name="strPosition")
    public void setPosition(String position) {
        this.position = position;
    }


    public En_Gender getGender () {
        return En_Gender.fromLegacyId (sex);
    }

    @Override
    public String toString() {
        return new StringBuilder("person{")
                .append(getId()).append("/")
                .append(HelperService.generateDisplayName(this))
                .append("}")
                .toString();
    }
}
