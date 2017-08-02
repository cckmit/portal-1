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
    private String retireOrder;
    private Date retireDate;
    private String startOrder;
    private Date startDate;

    private Long occupied;
    private Boolean isManager;
    private String bFoto;
    private String educationDocument;
    private String url;
    private String accepterList;
    private String inn;
    private Long medInsType;
    private String numberAuto;
    private String pension;
    private Date testDate;
    private Date startDateOfficial;
    private String icq;
    private Long personHoliday;
    private Long category;
    private Long jid;
    private Boolean alwaysSendNotifications;

    public ExternalPersonExtension() {}

    public ExternalPersonExtension(Person person) {

        setId (person.getId ());
        setPersonId (person.getId ());
        setDepartmentId (null);
        setPositionId (null);
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
        setRetireOrder ("");
        setRetireDate (null);
        setStartOrder ("");
        setStartDate (null);

        setOccupied (new Long (0));
        setManager (false);
        setFoto ("<NULL>");
        setEducationDocument ("");
        setUrl ("");
        setAccepterList ("");
        setInn ("");
        setMedInsType (new Long (0));
        setNumberAuto ("");
        setPension ("");
        setTestDate (null);
        setStartDateOfficial (null);
        setIcq ("");
        setPersonHoliday (new Long(0));
        setCategory (new Long(0));
        setJid (new Long(0));
        setAlwaysSendNotifications (false);
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

    @Column(name="strRetireOrder")
    public String getRetireOrder() {
        return retireOrder;
    }

    @Column(name="strRetireOrder")
    public void setRetireOrder(String retireOrder) {
        this.retireOrder = retireOrder;
    }

    @Column(name = "dRetire")
    public Date getRetireDate() {
        return retireDate;
    }

    @Column(name = "dRetire")
    public void setRetireDate(Date retireDate) {
        this.retireDate = retireDate;
    }

    @Column(name="strStartOrder")
    public String getStartOrder() {
        return startOrder;
    }

    @Column(name="strStartOrder")
    public void setStartOrder(String startOrder) {
        this.startOrder = startOrder;
    }

    @Column(name="dStart")
    public Date getStartDate() {
        return startDate;
    }

    @Column(name="dStart")
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name="strWorkTel")
    public String getWorkTel() {
        return workTel;
    }

    @Column(name="strWorkTel")
    public void setWorkTel(String workTel) {
        this.workTel = workTel;
    }

    @Column(name="nOccupied")
    public Long getOccupied() {
        return occupied;
    }

    @Column(name="nOccupied")
    public void setOccupied(Long occupied) {
        this.occupied = occupied;
    }

    @Column(name="lIsManager")
    public Boolean isManager() {
        return isManager;
    }

    @Column(name="lIsManager")
    public void setManager(Boolean isManager) {
        this.isManager = isManager;
    }

    @Column(name="bFoto")
    public String getFoto() {
        return bFoto;
    }

    @Column(name="bFoto")
    public void setFoto(String bFoto) {
        this.bFoto = bFoto;
    }

    @Column(name="strEducationDocument")
    public String getEducationDocument() {
        return educationDocument;
    }

    @Column(name="strEducationDocument")
    public void setEducationDocument(String educationDocument) {
        this.educationDocument = educationDocument;
    }

    @Column(name="strURL")
    public String getUrl() {
        return url;
    }

    @Column(name="strURL")
    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name="strAccepterList")
    public String getAccepterList() {
        return accepterList;
    }

    @Column(name="strAccepterList")
    public void setAccepterList(String accepterList) {
        this.accepterList = accepterList;
    }

    @Column(name="strINN")
    public String getInn() {
        return inn;
    }

    @Column(name="strINN")
    public void setInn(String inn) {
        this.inn = inn;
    }

    @Column(name="nMedInsType")
    public Long getMedInsType() {
        return medInsType;
    }

    @Column(name="nMedInsType")
    public void setMedInsType(Long medInsType) {
        this.medInsType = medInsType;
    }

    @Column(name="strNumberAuto")
    public String getNumberAuto() {
        return numberAuto;
    }

    @Column(name="strNumberAuto")
    public void setNumberAuto(String numberAuto) {
        this.numberAuto = numberAuto;
    }

    @Column(name="strPension")
    public String getPension() {
        return pension;
    }

    @Column(name="strPension")
    public void setPension(String pension) {
        this.pension = pension;
    }

    @Column(name = "dTestDate")
    public Date getTestDate() {
        return testDate;
    }

    @Column(name = "dTestDate")
    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    @Column(name = "dOficialStart")
    public Date getStartDateOfficial() {
        return startDateOfficial;
    }

    @Column(name = "dOficialStart")
    public void setStartDateOfficial(Date startDateOfficial) {
        this.startDateOfficial = startDateOfficial;
    }

    @Column(name="strICQ")
    public String getIcq() {
        return icq;
    }

    @Column(name="strICQ")
    public void setIcq(String icq) {
        this.icq = icq;
    }

    @Column(name="nPersonHoliday")
    public Long getPersonHoliday() {
        return personHoliday;
    }

    @Column(name="nPersonHoliday")
    public void setPersonHoliday(Long personHoliday) {
        this.personHoliday = personHoliday;
    }

    @Column(name="nCategory")
    public Long getCategory() {
        return category;
    }

    @Column(name="nCategory")
    public void setCategory(Long category) {
        this.category = category;
    }

    @Column(name="nJID")
    public Long getJid() {
        return jid;
    }

    @Column(name="nJID")
    public void setJid(Long jid) {
        this.jid = jid;
    }

    @Column(name="lAlwaysSendNotifications")
    public Boolean getAlwaysSendNotifications() {
        return alwaysSendNotifications;
    }

    @Column(name="lAlwaysSendNotifications")
    public void setAlwaysSendNotifications(Boolean alwaysSendNotifications) {
        this.alwaysSendNotifications = alwaysSendNotifications;
    }
}
