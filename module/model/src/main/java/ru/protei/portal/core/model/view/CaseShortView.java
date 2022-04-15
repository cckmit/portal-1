package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_WorkTrigger;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.ent.CaseObject.Columns.*;

/**
 * Сокращенное представление кейса
 */
@JdbcEntity(table = "case_object")
public class CaseShortView implements Serializable, Identifiable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = CaseObject.Columns.CASE_TYPE)
    private int typeId;

    @JdbcColumn(name = "CASENO")
    private Long caseNumber;

    @JdbcColumn(name = "CREATED")
    private Date created;

    @JdbcColumn(name = "MODIFIED")
    private Date modified;

    @JdbcColumn(name = "CASE_NAME")
    private String name;

    @JdbcColumn(name = "INFO")
    private String info;

    @JdbcColumn(name = "STATE")
    private long stateId;

    @JdbcJoinedColumn(mappedColumn = "STATE", table = "case_state", localColumn = "STATE", remoteColumn = "id" )
    private String stateName;

    @JdbcJoinedColumn(localColumn = "STATE", remoteColumn = "id", table = "case_state", mappedColumn = "color")
    private String stateColor;

    @JdbcColumn(name = "IMPORTANCE")
    private Integer impLevel;

    @JdbcJoinedColumn(localColumn = "IMPORTANCE", remoteColumn = "id", table = "importance_level", mappedColumn = "code")
    private String importanceCode;

    @JdbcJoinedColumn(localColumn = "IMPORTANCE", remoteColumn = "id", table = "importance_level", mappedColumn = "color")
    private String importanceColor;

    @JdbcColumn(name = "private_flag")
    private boolean privateCase;

    @JdbcColumn(name = "INITIATOR")
    private Long initiatorId;

    @JdbcJoinedColumn( mappedColumn = "displayname", table = "person", localColumn = "INITIATOR", remoteColumn = "ID" )
    private String initiatorName;

    @JdbcJoinedColumn( table = "person", localColumn = "INITIATOR", remoteColumn = "ID", mappedColumn = "displayShortName")
    private String initiatorShortName;

    @JdbcColumn(name = "initiator_company")
    private Long initiatorCompanyId;

    @JdbcJoinedColumn( table="company", localColumn = "initiator_company", remoteColumn = "id", mappedColumn = "cname")
    private String initiatorCompanyName;

    @JdbcColumn(name = "product_id")
    private Long productId;

    @JdbcJoinedColumn(table = "dev_unit", localColumn = "product_id", remoteColumn = "id", mappedColumn = "UNIT_NAME")
    private String productName;

    @JdbcColumn(name = "MANAGER")
    private Long managerId;

    @JdbcJoinedColumn( table = "person", localColumn = "MANAGER", remoteColumn = "id", mappedColumn = "displayname" )
    private String managerName;

    @JdbcJoinedColumn( table = "person", localColumn = "MANAGER", remoteColumn = "ID", mappedColumn = "displayShortName")
    private String managerShortName;

    @JdbcColumn(name = "ATTACHMENT_EXISTS")
    private boolean isAttachmentExists;

    @JdbcColumn(name = CaseObject.Columns.PAUSE_DATE)
    private Long pauseDate;

    @JdbcColumn(name = "manager_company_id")
    private Long managerCompanyId;

    @JdbcJoinedColumn(localColumn = "manager_company_id", remoteColumn = "id", table = "company", mappedColumn = "cname")
    private String managerCompanyName;

    @JdbcColumn(name = DEADLINE)
    private Long deadline;

    @JdbcColumn(name = AUTO_CLOSE)
    private Boolean autoClose;

    @JdbcColumn(name = WORK_TRIGGER)
    @JdbcEnumerated(EnumType.ID)
    private En_WorkTrigger workTrigger;

    @JdbcJoinedColumn( table="company", localColumn = "initiator_company", remoteColumn = "id", mappedColumn = "auto_open_issue")
    private Boolean autoOpenIssue;

    @JdbcColumn(name = EXT_APP)
    private String extAppType;

    // ManyToMany via CaseTagService
    private List<CaseTag> tags;

    //    Проставляется относительно авторизованного пользователя
    private boolean isFavorite;

    //  Проставляется при запросе обращений
    private boolean isPublicAttachmentExist;

    public CaseShortView() {

    }

    public String defGUID () {
        En_CaseType t = En_CaseType.find(this.typeId);
        return t != null ? t.makeGUID(this.caseNumber) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId( int typeId ) {
        this.typeId = typeId;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber( Long caseNumber ) {
        this.caseNumber = caseNumber;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public String getName () { return name; }

    public void setName ( String name ) { this.name = name; }

    public String getInfo() {
        return info;
    }

    public void setInfo( String info ) {
        this.info = info;
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

    public String getImportanceColor() {
        return importanceColor;
    }

    public void setImportanceColor(String importanceColor) {
        this.importanceColor = importanceColor;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId( Long initiatorId ) {
        this.initiatorId = initiatorId;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName( String initiatorName ) {
        this.initiatorName = initiatorName;
    }

    public String getInitiatorShortName() {
        return initiatorShortName;
    }

    public void setInitiatorShortName( String initiatorShortName ) {
        this.initiatorShortName = initiatorShortName;
    }

    public Long getInitiatorCompanyId() {
        return initiatorCompanyId;
    }

    public void setInitiatorCompanyId( Long initiatorCompanyId ) {
        this.initiatorCompanyId = initiatorCompanyId;
    }

    public String getInitiatorCompanyName() {
        return initiatorCompanyName;
    }

    public void setInitiatorCompanyName( String initiatorCompanyName ) {
        this.initiatorCompanyName = initiatorCompanyName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId( Long productId ) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName( String productName ) {
        this.productName = productName;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId( Long managerId ) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName( String managerName ) {
        this.managerName = managerName;
    }

    public String getManagerShortName() {
        return managerShortName;
    }

    public void setManagerShortName( String managerShortName ) {
        this.managerShortName = managerShortName;
    }

    public String getManagerCompanyName() {
        return managerCompanyName;
    }

    public void setManagerCompanyName( String managerCompanyName ) {
        this.managerCompanyName = managerCompanyName;
    }

    public boolean isPrivateCase() {
        return privateCase;
    }

    public void setPrivateCase( boolean privateCase ) {
        this.privateCase = privateCase;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified( Date modified ) {
        this.modified = modified;
    }

    public boolean isAttachmentExists() {
        return isAttachmentExists;
    }

    public void setAttachmentExists(boolean attachmentExists) {
        isAttachmentExists = attachmentExists;
    }

    public Long getPauseDate() {
        return pauseDate;
    }

    public void setPauseDate(Long pauseDate) {
        this.pauseDate = pauseDate;
    }

    public List<CaseTag> getTags() {
        return tags;
    }

    public void setTags(List<CaseTag> tags) {
        this.tags = tags;
    }

    public Long getManagerCompanyId() {
        return managerCompanyId;
    }

    public void setManagerCompanyId(Long managerCompanyId) {
        this.managerCompanyId = managerCompanyId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isPublicAttachmentsExist() {
        return isPublicAttachmentExist;
    }

    public void setPublicAttachmentsExist(boolean isPublicAttachmentExist) {
        this.isPublicAttachmentExist = isPublicAttachmentExist;
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

    public Boolean getAutoOpenIssue() {
        return autoOpenIssue;
    }

    public void setAutoOpenIssue(Boolean autoOpenIssue) {
        this.autoOpenIssue = autoOpenIssue;
    }

    public String getExtAppType() {
        return extAppType;
    }

    public void setExtAppType(String extAppType) {
        this.extAppType = extAppType;
    }

    public Boolean getAutoClose() {
        return autoClose;
    }

    public void setAutoClose(Boolean autoClose) {
        this.autoClose = autoClose;
    }

    @Override
    public boolean equals(Object obj) {
        if (id != null) {
            return obj instanceof CaseShortView && id.equals(((CaseShortView) obj).getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "CaseShortView{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", caseNumber=" + caseNumber +
                ", created=" + created +
                ", modified=" + modified +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", stateId=" + stateId +
                ", stateName='" + stateName + '\'' +
                ", impLevel=" + impLevel +
                ", privateCase=" + privateCase +
                ", initiatorId=" + initiatorId +
                ", initiatorName='" + initiatorName + '\'' +
                ", initiatorShortName='" + initiatorShortName + '\'' +
                ", initiatorCompanyId=" + initiatorCompanyId +
                ", initiatorCompanyName='" + initiatorCompanyName + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", managerId=" + managerId +
                ", managerName='" + managerName + '\'' +
                ", managerShortName='" + managerShortName + '\'' +
                ", isAttachmentExists=" + isAttachmentExists +
                ", pauseDate=" + pauseDate +
                ", managerCompanyId=" + managerCompanyId +
                ", managerCompanyName='" + managerCompanyName + '\'' +
                ", deadline=" + deadline +
                ", workTrigger=" + workTrigger +
                ", autoOpenIssue=" + autoOpenIssue +
                ", extAppType='" + extAppType + '\'' +
                ", tags=" + tags +
                ", isFavorite=" + isFavorite +
                ", isPublicAttachmentExist=" + isPublicAttachmentExist +
                '}';
    }
}
