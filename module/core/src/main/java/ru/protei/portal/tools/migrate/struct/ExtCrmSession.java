package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.LegacyEntity;

import java.util.Date;

@Table(name = "CRM.Tm_Session")
public class ExtCrmSession implements LegacyEntity {

    @PrimaryKey
    @Column(name = "nID")
    private Long id;

    @Column(name = "dtCreation")
    private Date created;

    @Column(name = "strCreator")
    private String creator;

    @Column(name = "strClientLogin")
    private String clientLogin;

    @Column(name = "strClientIp")
    private String clientIp;

    @Column(name = "nCategoryId")
    private int categoryId;

    @Column(name = "nProductId")
    private Long productId;

    @Column(name = "nCompanyId")
    private long companyId;

    @Column(name = "strDescription")
    private String description;

    @Column(name = "dtSession")
    private Date sessionStarted;

    @Column(name = "nManagerId")
    private Long managerId;

    @Column(name = "lDeleted")
    private Integer deletedFlag;

    @Column(name = "nStatusId")
    private long statusId;

    @Column(name = "strRecipients")
    private String recipients;

    @Column(name = "nCriticalityID")
    private Integer importance;

    @Column(name = "lPrivate")
    private int privateFlag;

    @Column(name = "lUpgradeSW")
    private Integer upgradeSoftwareFlag;

    @Column(name = "dtLastUpdate")
    private Date lastUpdate;


    public ExtCrmSession() {
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

    public String getClientLogin() {
        return clientLogin;
    }

    public void setClientLogin(String clientLogin) {
        this.clientLogin = clientLogin;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getSessionStarted() {
        return sessionStarted;
    }

    public void setSessionStarted(Date sessionStarted) {
        this.sessionStarted = sessionStarted;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Integer getDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(Integer deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    public long getStatusId() {
        return statusId;
    }

    public void setStatusId(long statusId) {
        this.statusId = statusId;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public int getPrivateFlag() {
        return privateFlag;
    }

    public void setPrivateFlag(int privateFlag) {
        this.privateFlag = privateFlag;
    }

    public Integer getUpgradeSoftwareFlag() {
        return upgradeSoftwareFlag;
    }

    public void setUpgradeSoftwareFlag(Integer upgradeSoftwareFlag) {
        this.upgradeSoftwareFlag = upgradeSoftwareFlag;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    public boolean isDeleted () {
        return this.deletedFlag != null && this.deletedFlag != 0;
    }

    public boolean isPrivate () {
        return this.privateFlag != 0;
    }
}
