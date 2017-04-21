package ru.protei.portal.hpsm;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import protei.utils.common.ThreadLocalDateFormat;

import java.util.Date;

/**
 * Created by michael on 19.04.17.
 */
@XStreamAlias("FIELDS")
public class EventMsg {

    private static ThreadLocalDateFormat DATE_FMT = new ThreadLocalDateFormat("dd/MM/yyyy HH:mm:ss");

    @XStreamAlias("ID_HPSM")
    String hpsmId;

    @XStreamAlias("STATUS")
    String statusText;

    @XStreamAlias("ID_VENDOR")
    String ourId;

    @XStreamAlias("ASSIGNMENT")
    String contactPerson;

    @XStreamAlias("ASSIGNMENT_EMAIL")
    String contactPersonEmail;

    @XStreamAlias("FILIAL")
    String departmentName;

    @XStreamAlias("REGION")
    String geoRegion;

    @XStreamAlias("CITY")
    String city;

    @XStreamAlias("ADDRESS")
    String address;

    @XStreamAlias("SITE_NAME")
    String siteName;

    @XStreamAlias("PRODUCT_TYPE")
    String productName;

    @XStreamAlias("MANUFACTURER")
    String manufacturer;

    @XStreamAlias("MODEL")
    String model;

    @XStreamAlias("LOGICAL_NAME")
    String logicalName;

    @XStreamAlias("SOFT_VERSION")
    String version;

    @XStreamAlias("SEVERITY")
    int severity;

    @XStreamAlias("BRIEF_DESCRIPTION")
    String subject;

    @XStreamAlias("DESCRIPTION")
    String description;

    @XStreamAlias("MESSAGE")
    String message;

    @XStreamAlias("DISPUTE")
    String dispute;

    @XStreamAlias("REGISTRATION_TIME")
    String txRegistrationTime;

    @XStreamAlias("TO_WORK_TIME")
    String txOpenTime;

    @XStreamAlias("WORKAROUND_TIME")
    String txWorkaroundTime;

    @XStreamAlias("RESOLUTION_TIME")
    String txSolutionTime;

    @XStreamAlias("ACCEPT_WORKAROUND_TIME")
    String txAcceptWorkaroundTime;

    public EventMsg () {

    }

    public String getHpsmId() {
        return hpsmId;
    }

    public void setHpsmId(String hpsmId) {
        this.hpsmId = hpsmId;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getOurId() {
        return ourId;
    }

    public void setOurId(String ourId) {
        this.ourId = ourId;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getGeoRegion() {
        return geoRegion;
    }

    public void setGeoRegion(String geoRegion) {
        this.geoRegion = geoRegion;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLogicalName() {
        return logicalName;
    }

    public void setLogicalName(String logicalName) {
        this.logicalName = logicalName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDispute() {
        return dispute;
    }

    public void setDispute(String dispute) {
        this.dispute = dispute;
    }

    public String getTxRegistrationTime() {
        return txRegistrationTime;
    }

    public void setTxRegistrationTime(String txRegistrationTime) {
        this.txRegistrationTime = txRegistrationTime;
    }

    public String getTxOpenTime() {
        return txOpenTime;
    }

    public void setTxOpenTime(String txOpenTime) {
        this.txOpenTime = txOpenTime;
    }

    public String getTxWorkaroundTime() {
        return txWorkaroundTime;
    }

    public void setTxWorkaroundTime(String txWorkaroundTime) {
        this.txWorkaroundTime = txWorkaroundTime;
    }

    public String getTxSolutionTime() {
        return txSolutionTime;
    }

    public void setTxSolutionTime(String txSolutionTime) {
        this.txSolutionTime = txSolutionTime;
    }

    public String getTxAcceptWorkaroundTime() {
        return txAcceptWorkaroundTime;
    }

    public void setTxAcceptWorkaroundTime(String txAcceptWorkaroundTime) {
        this.txAcceptWorkaroundTime = txAcceptWorkaroundTime;
    }

    public void setRegistrationTime (Date d) {
        this.txRegistrationTime = d == null ? null : DATE_FMT.format(d);
    }

    public void setOpenTime (Date d) {
        this.txOpenTime = d == null ? null : DATE_FMT.format(d);
    }

    public void setWorkaroundTime (Date d) {
        this.txWorkaroundTime = d == null ? null : DATE_FMT.format(d);
    }

    public void setSolutionTime (Date d) {
        this.txSolutionTime = d == null ? null : DATE_FMT.format(d);
    }

    public void setConfirmWaTime (Date d) {
        this.txAcceptWorkaroundTime = d == null ? null : DATE_FMT.format(d);
    }

//    public Date getRegistrationTime () {
//        return DATE_FMT.parse(txRegistrationTime);
//    }
}
