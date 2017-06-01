package ru.protei.portal.hpsm.struct;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.hpsm.api.HpsmSeverity;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by michael on 19.04.17.
 */
@XStreamAlias("FIELDS")
public class HpsmMessage implements Cloneable,Serializable {

    @XStreamAlias("ID_HPSM")
    String hpsmId;

    @XStreamAlias("STATUS")
    String statusText;

    @XStreamAlias("ID_VENDOR")
    String ourId;

    @XStreamAlias("ASSIGNMENT")
    String contactPerson;

    @XStreamAlias("V_ASSIGNMENT")
    String ourManager;

    @XStreamAlias("ASSIGNMENT_EMAIL")
    String contactPersonEmail;

    @XStreamAlias("V_ASSIGNMENT_EMAIL")
    String ourManagerEmail;

    @XStreamAlias("FILIAL")
    String companyBranch;

    @XStreamAlias("REGION")
    String geoRegion;

    @XStreamAlias("CITY")
    String city;

    @XStreamAlias("ADDRESS")
    String address;

    @XStreamAlias("SITE_NAME")
    String webSite;

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
    String shortDescription;

    @XStreamAlias("DESCRIPTION")
    String description;

    @XStreamAlias("MESSAGE")
    String message;

    @XStreamAlias("DISPUTE")
    String dispute;

    @XStreamAlias("REGISTRATION_TIME")
    String txRegistrationTime;

    @XStreamAlias("V_REGISTRATION_TIME")
    String txOurRegistrationTime;

    @XStreamAlias("TO_WORK_TIME")
    String txOpenTime;

    @XStreamAlias("V_TO_WORK_TIME")
    String txOurOpenTime;

    @XStreamAlias("WORKAROUND_TIME")
    String txWorkaroundTime;

    @XStreamAlias("V_WORKAROUND_TIME")
    String txOurWorkaroundTime;

    @XStreamAlias("RESOLUTION_TIME")
    String txSolutionTime;

    @XStreamAlias("V_RESOLUTION_TIME")
    String txOurSolutionTime;

    @XStreamAlias("ACCEPT_WORKAROUND_TIME")
    String txAcceptWorkaroundTime;


    @XStreamAlias("WORKAROUND")
    String workaroundText;

    @XStreamAlias("RESOLUTION")
    String resolutionText;

    public HpsmMessage() {

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

    public String getCompanyBranch() {
        return companyBranch;
    }

    public void setCompanyBranch(String companyBranch) {
        this.companyBranch = companyBranch;
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

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
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

    public String getOurManager() {
        return ourManager;
    }

    public void setOurManager(String ourManager) {
        this.ourManager = ourManager;
    }

    public String getOurManagerEmail() {
        return ourManagerEmail;
    }

    public void setOurManagerEmail(String ourManagerEmail) {
        this.ourManagerEmail = ourManagerEmail;
    }

    public String getTxOurRegistrationTime() {
        return txOurRegistrationTime;
    }

    public void setTxOurRegistrationTime(String txOurRegistrationTime) {
        this.txOurRegistrationTime = txOurRegistrationTime;
    }

    public String getTxOurOpenTime() {
        return txOurOpenTime;
    }

    public void setTxOurOpenTime(String txOurOpenTime) {
        this.txOurOpenTime = txOurOpenTime;
    }

    public String getTxOurWorkaroundTime() {
        return txOurWorkaroundTime;
    }

    public void setTxOurWorkaroundTime(String txOurWorkaroundTime) {
        this.txOurWorkaroundTime = txOurWorkaroundTime;
    }

    public String getTxOurSolutionTime() {
        return txOurSolutionTime;
    }

    public void setTxOurSolutionTime(String txOurSolutionTime) {
        this.txOurSolutionTime = txOurSolutionTime;
    }

    public String getWorkaroundText() {
        return workaroundText;
    }

    public void setWorkaroundText(String workaroundText) {
        this.workaroundText = workaroundText;
    }

    public String getResolutionText() {
        return resolutionText;
    }

    public void setResolutionText(String resolutionText) {
        this.resolutionText = resolutionText;
    }

    public void setRegistrationTime (Date d) {
        this.txRegistrationTime = HpsmUtils.formatDate(d);
    }

    public void setOurRegistrationTime (Date d) {
        this.txOurRegistrationTime = HpsmUtils.formatDate(d);
    }

    public void setOpenTime (Date d) {
        this.txOpenTime = HpsmUtils.formatDate(d);
    }

    public void setOurOpenTime (Date d) {
        this.txOurOpenTime = HpsmUtils.formatDate(d);
    }

    public void setWorkaroundTime (Date d) {
        this.txWorkaroundTime = HpsmUtils.formatDate(d);
    }

    public void setOurWorkaroundTime (Date d) {
        this.txOurWorkaroundTime = HpsmUtils.formatDate(d);
    }

    public void setSolutionTime (Date d) {
        this.txSolutionTime = HpsmUtils.formatDate(d);
    }

    public void setOurSolutionTime (Date d) {
        this.txOurSolutionTime = HpsmUtils.formatDate(d);
    }

    public void setConfirmWaTime (Date d) {
        this.txAcceptWorkaroundTime = HpsmUtils.formatDate(d);
    }


    public void updateCustomerFields (HpsmMessage custState) {
        this.severity = custState.severity;
        this.txAcceptWorkaroundTime = custState.txAcceptWorkaroundTime;
        this.txOpenTime = custState.txOpenTime;
        this.txSolutionTime = custState.txSolutionTime;
        this.txWorkaroundTime = custState.txWorkaroundTime;

        if (HelperFunc.isNotEmpty(custState.contactPerson))
            this.contactPerson = custState.contactPerson;

        if (HelperFunc.isNotEmpty(custState.contactPersonEmail)) {
            this.contactPersonEmail = custState.contactPersonEmail;
        }
    }


    public HpsmStatus status (HpsmStatus s) {
        this.statusText = s.getHpsmCode();
        return s;
    }

    public HpsmStatus status () {
        return HpsmStatus.parse(this.statusText);
    }

    public HpsmSeverity severity () {
        return HpsmSeverity.find(this.severity);
    }

    public HpsmSeverity severity (HpsmSeverity sv) {
        this.severity = sv.getHpsmLevel();
        return sv;
    }



    public HpsmMessage createCopy () {
        try {
            return (HpsmMessage) clone();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to clone event-message object", e);
        }
    }
}