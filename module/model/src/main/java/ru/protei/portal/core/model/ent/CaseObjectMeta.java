package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_WorkTrigger;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.ent.CaseObject.Columns.*;

@JdbcEntity(table = "case_object")
public class CaseObjectMeta extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    @JdbcColumn(name = "MODIFIED")
    private Date modified;

    // --------------------

    @JdbcColumn(name = "STATE")
    private long stateId;

    @JdbcJoinedColumn(localColumn = "STATE", table = "case_state", remoteColumn = "id", mappedColumn = "STATE")
    private String stateName;

    @JdbcJoinedColumn(localColumn = "STATE", table = "case_state", remoteColumn = "id", mappedColumn = "color")
    private String stateColor;

    @JdbcJoinedColumn(localColumn = "STATE", table = "case_state", remoteColumn = "id", mappedColumn = "INFO")
    private String stateInfo;

    @JdbcColumn(name = "IMPORTANCE")
    private Integer impLevel;

    @JdbcJoinedColumn(localColumn = "IMPORTANCE", remoteColumn = "id", table = "importance_level", mappedColumn = "code")
    private String importanceCode;

    @JdbcColumn(name = "INITIATOR")
    private Long initiatorId;

    @JdbcJoinedObject( localColumn = "INITIATOR", remoteColumn = "id", updateLocalColumn = false, sqlTableAlias = "PersonInitiator" )
    private Person initiator;

    @JdbcColumn(name = "initiator_company")
    private Long initiatorCompanyId;

    @JdbcJoinedObject( localColumn = "initiator_company", remoteColumn = "id", updateLocalColumn = false )
    private Company initiatorCompany;

    @JdbcColumn(name = "product_id")
    private Long productId;

    @JdbcJoinedObject(localColumn = "product_id", remoteColumn = "id", updateLocalColumn = false)
    private DevUnit product;

    @JdbcColumn(name = "MANAGER")
    private Long managerId;

    @JdbcJoinedObject( localColumn = "MANAGER", remoteColumn = "id", updateLocalColumn = false )
    private PersonShortView manager;

    @JdbcColumn(name = "platform_id")
    private Long platformId;

    @JdbcJoinedColumn(localColumn = "platform_id", table = "platform", remoteColumn = "id", mappedColumn = "name")
    private String platformName;

    @JdbcColumn(name = "time_elapsed")
    private Long timeElapsed;

    @JdbcColumn(name = "private_flag")
    private boolean privateCase;

    @JdbcColumn(name = CaseObject.Columns.EXT_APP)
    private String extAppType;

    @JdbcColumn(name = CaseObject.Columns.PAUSE_DATE)
    private Long pauseDate;

    @JdbcColumn(name = "manager_company_id")
    private Long managerCompanyId;

    @JdbcJoinedColumn(localColumn = "manager_company_id", remoteColumn = "id", table = "company", mappedColumn = "cname")
    private String managerCompanyName;

    @JdbcColumn(name = AUTO_CLOSE)
    private Boolean autoClose;

    @JdbcColumn(name = DEADLINE)
    private Long deadline;

    @JdbcColumn(name = WORK_TRIGGER)
    @JdbcEnumerated(EnumType.ID)
    private En_WorkTrigger workTrigger;

    //    not db column
    private List<Plan> plans;

    public CaseObjectMeta() {}

    public CaseObjectMeta(CaseObject co) {
        if (co == null) return;
        if (co.getId() != null) setId(co.getId());
        if (co.getModified() != null) setModified(co.getModified());
        if (co.getStateId() != 0) setStateId(co.getStateId());
        if (co.getStateName() != null) setStateName(co.getStateName());
        if (co.getStateColor() != null) setStateColor(co.getStateColor());
        if (co.getStateInfo() != null) setStateInfo(co.getStateInfo());
        if (co.getPauseDate() != null) setPauseDate(co.getPauseDate());
        if (co.getImpLevel() != null) setImpLevel(co.getImpLevel());
        if (co.getInitiator() != null) setInitiator(co.getInitiator());
        if (co.getInitiatorId() != null) setInitiatorId(co.getInitiatorId());
        if (co.getInitiatorCompany() != null) setInitiatorCompany(co.getInitiatorCompany());
        if (co.getInitiatorCompanyId() != null) setInitiatorCompanyId(co.getInitiatorCompanyId());
        if (co.getProduct() != null) setProduct(co.getProduct());
        if (co.getProductId() != null) setProductId(co.getProductId());
        if (co.getManager() != null) setManager(co.getManager());
        if (co.getManagerId() != null) setManagerId(co.getManagerId());
        if (co.getPlatformId() != null) setPlatformId(co.getPlatformId());
        if (co.getPlatformName() != null) setPlatformName(co.getPlatformName());
        if (co.getTimeElapsed() != null) setTimeElapsed(co.getTimeElapsed());
        if (co.getExtAppType() != null) setExtAppType(co.getExtAppType());
        if (co.getManagerCompanyId() != null) setManagerCompanyId(co.getManagerCompanyId());
        if (co.getManagerCompanyName() != null) setManagerCompanyName(co.getManagerCompanyName());
        if (co.getPlans() != null) setPlans(co.getPlans());
        if (co.getAutoClose() != null) setAutoClose(co.getAutoClose());
        if (co.getDeadline() != null) setDeadline(co.getDeadline());
        if (co.getWorkTrigger() != null) setWorkTrigger(co.getWorkTrigger());
        if (co.getImportanceCode() != null) setImportanceCode(co.getImportanceCode());
        setPrivateCase(co.isPrivateCase());
    }

    @Override
    public String getAuditType() {
        return "CaseObjectMeta";
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateColor() {
        return stateColor;
    }

    public void setStateColor(String stateColor) {
        this.stateColor = stateColor;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public Integer getImpLevel() {
        return impLevel;
    }

    public void setImpLevel(Integer impLevel) {
        this.impLevel = impLevel;
    }

    public String getImportanceCode() {
        return importanceCode;
    }

    public void setImportanceCode(String importanceCode) {
        this.importanceCode = importanceCode;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }

    public Person getInitiator() {
        return initiator;
    }

    public void setInitiator(Person initiator) {
        this.initiator = initiator;
        this.initiatorId = initiator == null ? null : initiator.getId();
    }

    public Long getInitiatorCompanyId() {
        return initiatorCompanyId;
    }

    public void setInitiatorCompanyId(Long initiatorCompanyId) {
        this.initiatorCompanyId = initiatorCompanyId;
    }

    public Company getInitiatorCompany() {
        return initiatorCompany;
    }

    public void setInitiatorCompany(Company company) {
        this.initiatorCompany = company;
        this.initiatorCompanyId = company == null ? null : company.getId();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public DevUnit getProduct() {
        return product;
    }

    public void setProduct(DevUnit product) {
        this.product = product;
        this.productId = product == null ? null : product.getId();
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public PersonShortView getManager() {
        return manager;
    }

    public void setManager(PersonShortView manager) {
        this.manager = manager;
        this.managerId = manager == null ? null : manager.getId();
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public void setPlatform(PlatformOption platform) {
        this.platformId = platform == null ? null : platform.getId();
        this.platformName = platform == null ? null : platform.getDisplayText();
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(Long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public boolean isPrivateCase() {
        return privateCase;
    }

    public void setPrivateCase(boolean privateCase) {
        this.privateCase = privateCase;
    }

    public String getExtAppType() {
        return extAppType;
    }

    public void setExtAppType(String extAppType) {
        this.extAppType = extAppType;
    }

    public Long getPauseDate() {
        return pauseDate;
    }

    public void setPauseDate(Long pauseDate) {
        this.pauseDate = pauseDate;
    }

    public Long getManagerCompanyId() {
        return managerCompanyId;
    }

    public void setManagerCompanyId(Long managerCompanyId) {
        this.managerCompanyId = managerCompanyId;
    }

    public void setManagerCompany(EntityOption managerCompany) {
        if (managerCompany == null) {
            managerCompanyId = null;
            managerCompanyName = null;
        } else {
            managerCompanyId = managerCompany.getId();
            managerCompanyName = managerCompany.getDisplayText();
        }
    }

    public String getManagerCompanyName() {
        return managerCompanyName;
    }

    public void setManagerCompanyName(String managerCompanyName) {
        this.managerCompanyName = managerCompanyName;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public Boolean getAutoClose() {
        return autoClose;
    }

    public void setAutoClose(Boolean autoClose) {
        this.autoClose = autoClose;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public En_WorkTrigger getWorkTrigger() {
        return workTrigger;
    }

    public void setWorkTrigger(En_WorkTrigger workTrigger) {
        this.workTrigger = workTrigger;
    }

    @Override
    public String toString() {
        return "CaseObjectMeta{" +
                "id=" + id +
                ", modified=" + modified +
                ", stateId=" + stateId +
                ", stateName='" + stateName + '\'' +
                ", stateColor='" + stateColor + '\'' +
                ", stateInfo='" + stateInfo + '\'' +
                ", impLevel=" + impLevel +
                ", initiatorId=" + initiatorId +
                ", initiator=" + initiator +
                ", initiatorCompanyId=" + initiatorCompanyId +
                ", initiatorCompany=" + initiatorCompany +
                ", productId=" + productId +
                ", product=" + product +
                ", managerId=" + managerId +
                ", manager=" + manager +
                ", platformId=" + platformId +
                ", platformName='" + platformName + '\'' +
                ", timeElapsed=" + timeElapsed +
                ", privateCase=" + privateCase +
                ", extAppType='" + extAppType + '\'' +
                ", pauseDate=" + pauseDate +
                ", managerCompanyId=" + managerCompanyId +
                ", managerCompanyName='" + managerCompanyName + '\'' +
                ", plans=" + plans +
                ", deadline=" + deadline +
                ", workTrigger=" + workTrigger +
                '}';
    }
}
