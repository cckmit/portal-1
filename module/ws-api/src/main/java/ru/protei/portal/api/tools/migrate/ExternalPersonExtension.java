package ru.protei.portal.api.tools.migrate;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

import java.util.Date;

/**
 * Created by turik on 08.09.16.
 */
@Table(name="\"Resource\".Tm_PersonPROTEI_Extension")
public class ExternalPersonExtension {

    private Long id;
    private Long personId;
    private Long departmentId;
    private Long positionId;
    private String email;
    private String otherEmail;
    private String fax;
    private String homeTel;
    private String mobileTel;
    private String workTel;
    private String actualAddress;
    private String officialAddress;
    private Boolean isRetired;
    private String ipAddress;
    //private Date retireDate;
    //private Boolean isManager;

    public ExternalPersonExtension() {}

    public ExternalPersonExtension(Person person) {

        setId (person.getId ());
        setPersonId (person.getId ());
        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(person.getContactInfo());
        setEmail (contactInfoFacade.getEmail ());
        setOtherEmail (contactInfoFacade.getEmail_own ());
        setFax (contactInfoFacade.getFax ());
        setHomeTel (contactInfoFacade.getHomePhone ());
        setMobileTel (contactInfoFacade.getMobilePhone ());
        setWorkTel (contactInfoFacade.getWorkPhone ());
        setActualAddress (contactInfoFacade.getHomeAddress ());
        setOfficialAddress (contactInfoFacade.getLegalAddress());
        setRetired (person.isFired ());
        setIpAddress (person.getIpAddress ());
        //setRetireDate (null);
        //setManager (false);
    }

    @PrimaryKey
    @Column(key=true, name="nID")
    public Long getId() {
        return id;
    }

    @PrimaryKey
    @Column(key=true, name="nID")
    public void setId(Long id) {
        this.id = id;
    }

    @Column(key=true, name="nPersonID")
    public Long getPersonId() {
        return personId;
    }

    @Column(key=true, name="nPersonID")
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Column(name="nDepartmentID")
    public Long getDepartmentId() {
        return departmentId;
    }

    @Column(name="nDepartmentID")
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    @Column(name="strE_Mail")
    public String getEmail() {
        return email;
    }

    @Column(name="strE_Mail")
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name="strFaxTel")
    public String getFax() {
        return fax;
    }

    @Column(name="strFaxTel")
    public void setFax(String fax) {
        this.fax = fax;
    }

    @Column(name="strHomeTel")
    public String getHomeTel() {
        return homeTel;
    }

    @Column(name="strHomeTel")
    public void setHomeTel(String homeTel) {
        this.homeTel = homeTel;
    }

    @Column(name="strActualAddress")
    public String getActualAddress() {
        return actualAddress;
    }

    @Column(name="strActualAddress")
    public void setActualAddress(String actualAddress) {
        this.actualAddress = actualAddress;
    }

    @Column(name="lRetired")
    public Boolean isRetired() {
        return isRetired;
    }

    @Column(name="lRetired")
    public void setRetired(Boolean isRetired) {
        this.isRetired = isRetired;
    }

    @Column(name="strMobileTel")
    public String getMobileTel() {
        return mobileTel;
    }

    @Column(name="strMobileTel")
    public void setMobileTel(String mobileTel) {
        this.mobileTel = mobileTel;
    }

    @Column(name="strOficialAddress")
    public String getOfficialAddress() {
        return officialAddress;
    }

    @Column(name="strOficialAddress")
    public void setOfficialAddress(String officialAddress) {
        this.officialAddress = officialAddress;
    }

    @Column(name="strOther_E_Mail")
    public String getOtherEmail() {
        return otherEmail;
    }

    @Column(name="strOther_E_Mail")
    public void setOtherEmail(String otherEmail) {
        this.otherEmail = otherEmail;
    }

    @Column(name="strIP_Address")
    public String getIpAddress() {
        return ipAddress;
    }

    @Column(name="strIP_Address")
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Column(name="nPositionID")
    public Long getPositionId() {
        return positionId;
    }

    @Column(name="nPositionID")
    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    @Column(name="strWorkTel")
    public String getWorkTel() {
        return workTel;
    }

    @Column(name="strWorkTel")
    public void setWorkTel(String workTel) {
        this.workTel = workTel;
    }
}
