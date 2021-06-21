package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.LegacyEntity;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

import java.util.Date;

/**
 * Created by turik on 08.09.16.
 */
@Table(name="\"Resource\".Tm_PersonPROTEI_Extension",starQuery = false)
public class ExternalPersonExtension implements LegacyEntity {

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
    private Date retireDate;
    private String ipAddress;

    private String inn = "";
    private String pension = "";
    private String url = "";
    private String icq = "";
    private String startOrder = "";
    private String retireOrder = "";
    private String educationDocument = "";


    public ExternalPersonExtension() {}

    public ExternalPersonExtension(Person person) {
        setId (person.getOldId());
        setPersonId (person.getOldId());
        updateFromPerson(person);
    }

    public void updateFromPerson(Person person) {
        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(person.getContactInfo());
        setEmail (contactInfoFacade.getEmail () == null ? "" : contactInfoFacade.getEmail ());
        setOtherEmail (contactInfoFacade.getEmail_own () == null ? "" : contactInfoFacade.getEmail_own ());
        setFax (contactInfoFacade.getFax () == null ? "" : contactInfoFacade.getFax ());
        setHomeTel (contactInfoFacade.getHomePhone () == null ? "" : contactInfoFacade.getHomePhone ());
        setMobileTel (contactInfoFacade.getMobilePhone () == null ? "" : contactInfoFacade.getMobilePhone ());
        setWorkTel (contactInfoFacade.getWorkPhone () == null ? "" : contactInfoFacade.getWorkPhone ());
        setActualAddress (contactInfoFacade.getHomeAddress () == null ? "" : contactInfoFacade.getHomeAddress ());
        setOfficialAddress (contactInfoFacade.getLegalAddress() == null ? "" : contactInfoFacade.getLegalAddress());
        setRetired (person.isFired ());
        setRetireDate (person.isFired() ? person.getFireDate() : null);
        setIpAddress (person.getIpAddress () == null ? "" : person.getIpAddress ());
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

    @Column(name="nPersonID")
    public Long getPersonId() {
        return personId;
    }

    @Column(name="nPersonID")
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

    @Column(name="strINN")
    public String getInn() {
        return inn;
    }

    @Column(name="strINN")
    public void setInn(String inn) {
        this.inn = inn;
    }

    @Column(name="strPension")
    public String getPension() {
        return pension;
    }

    @Column(name="strPension")
    public void setPension(String pension) {
        this.pension = pension;
    }

    @Column(name="strURL")
    public String getUrl() {
        return url;
    }

    @Column(name="strURL")
    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name="strICQ")
    public String getIcq() {
        return icq;
    }

    @Column(name="strICQ")
    public void setIcq(String icq) {
        this.icq = icq;
    }

    @Column(name="strStartOrder")
    public String getStartOrder() {
        return startOrder;
    }

    @Column(name="strStartOrder")
    public void setStartOrder(String startOrder) {
        this.startOrder = startOrder;
    }

    @Column(name="strRetireOrder")
    public String getRetireOrder() {
        return retireOrder;
    }

    @Column(name="strRetireOrder")
    public void setRetireOrder(String retireOrder) {
        this.retireOrder = retireOrder;
    }

    @Column(name="strEducationDocument")
    public String getEducationDocument() {
        return educationDocument;
    }

    @Column(name="strEducationDocument")
    public void setEducationDocument(String educationDocument) {
        this.educationDocument = educationDocument;
    }

    @Column(name="dRetire")
    public Date getRetireDate() {
        return retireDate;
    }

    @Column(name="dRetire")
    public void setRetireDate(Date retireDate) {
        this.retireDate = retireDate;
    }

    public boolean isFired () {
        return this.isRetired != null && this.isRetired.booleanValue();
    }
}
