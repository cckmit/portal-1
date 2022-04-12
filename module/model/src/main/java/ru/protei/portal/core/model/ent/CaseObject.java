package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.dict.En_WorkTrigger;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.core.model.ent.CaseObject.Columns.*;

/**
 * Created by michael on 19.05.16.
 */
@JdbcEntity(table = "case_object")
public class CaseObject extends AuditableObject {

    @JdbcId(name = ID, idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = CASE_TYPE)
    @JdbcEnumerated( EnumType.ID )
    private En_CaseType type;

    @JdbcColumn(name = CASENO)
    private Long caseNumber;

    @JdbcColumn(name = CREATED)
    private Date created;

    @JdbcColumn(name = MODIFIED)
    private Date modified;

    @JdbcColumn(name = CASE_NAME)
    private String name;

    @JdbcColumn(name = "EXT_ID")
    private String extId;

    @JdbcColumn(name = INFO)
    private String info;

    @JdbcColumn(name = STATE)
    private long stateId;

    @JdbcJoinedColumn(localColumn = STATE, table = "case_state", remoteColumn = "id", mappedColumn = "STATE")
    private String stateName;

    @JdbcJoinedColumn(localColumn = STATE, table = "case_state", remoteColumn = "id", mappedColumn = "color")
    private String stateColor;

    @JdbcJoinedColumn(localColumn = STATE, table = "case_state", remoteColumn = "id", mappedColumn = "INFO")
    private String stateInfo;

    @JdbcColumn(name = IMPORTANCE)
    private Integer impLevel;

    @JdbcJoinedColumn(localColumn = IMPORTANCE, remoteColumn = "id", table = "importance_level", mappedColumn = "code")
    private String importanceCode;

    @JdbcColumn(name = CREATOR)
    private Long creatorId;

    @JdbcJoinedObject( localColumn = "CREATOR", remoteColumn = "id", updateLocalColumn = false )
    private Person creator;

    @JdbcColumn(name = "CREATOR_IP")
    private String creatorIp;

    @JdbcColumn(name = INITIATOR)
    private Long initiatorId;

    @JdbcJoinedObject( localColumn = INITIATOR, remoteColumn = "id", updateLocalColumn = false, sqlTableAlias = "PersonInitiator" )
    private Person initiator;

    @JdbcColumn(name = "initiator_company")
    private Long initiatorCompanyId;

    @JdbcJoinedObject( localColumn = INITIATOR_COMPANY, remoteColumn = "id", updateLocalColumn = false )
    private Company initiatorCompany;

    @JdbcColumn(name = PRODUCT_ID)
    private Long productId;

    @JdbcJoinedObject(localColumn = PRODUCT_ID, remoteColumn = "id", updateLocalColumn = false)
    private DevUnit product;

    @JdbcColumn(name = MANAGER)
    private Long managerId;

    @JdbcJoinedObject( localColumn = MANAGER, remoteColumn = "id", updateLocalColumn = false )
    private PersonShortView manager;

    @JdbcColumn(name = "KEYWORDS")
    private String keywords;

    @JdbcColumn(name = "EMAILS")
    private String emails;

    @JdbcColumn(name = "creator_info")
    private String creatorInfo;

    @JdbcColumn(name = DELETED)
    private boolean deleted;

    @JdbcColumn(name = "private_flag")
    private boolean privateCase;

    @JdbcColumn(name = "ATTACHMENT_EXISTS")
    private boolean isAttachmentExists;

    @JdbcManyToMany(linkTable = "case_attachment", localLinkColumn = "case_id", remoteLinkColumn = "att_id")
    private List<Attachment> attachments;

    @JdbcOneToMany(table = "case_location", localColumn = "id", remoteColumn = "CASE_ID" )
    private List<CaseLocation> locations;

    @JdbcOneToMany( table = "case_member", localColumn = "id", remoteColumn = "CASE_ID" )
    private List<CaseMember> members;

    @JdbcColumn(name = EXT_APP)
    private String extAppType;

    @JdbcManyToMany(linkTable = "case_notifier", localLinkColumn = "case_id", remoteLinkColumn = "person_id")
    private Set<Person> notifiers; //may contain partially filled objects!

    @JdbcColumn(name = "time_elapsed")
    private Long timeElapsed;

    @JdbcColumn(name = PLATFORM_ID)
    private Long platformId;

    @JdbcJoinedColumn(localColumn = PLATFORM_ID, table = "platform", remoteColumn = "id", mappedColumn = "name")
    private String platformName;

    @JdbcColumn(name = PAUSE_DATE)
    private Long pauseDate;

    @JdbcColumn(name = MANAGER_COMPANY_ID)
    private Long managerCompanyId;

    @JdbcJoinedColumn(localColumn = MANAGER_COMPANY_ID, remoteColumn = "id", table = "company", mappedColumn = "cname")
    private String managerCompanyName;

    @JdbcManyToMany(localLinkColumn = "case_object_id", remoteLinkColumn = "plan_id", linkTable = "plan_to_case_object")
    private List<Plan> plans;

    @JdbcColumn(name = AUTO_CLOSE)
    private Boolean autoClose;

    @JdbcColumn(name = DEADLINE)
    private Long deadline;

    @JdbcColumn(name = WORK_TRIGGER)
    @JdbcEnumerated(EnumType.ID)
    private En_WorkTrigger workTrigger;

    // not db column
    private En_TimeElapsedType timeElapsedType;

    // not db column
    private CaseObjectMetaJira caseObjectMetaJira;

    // not db column
    private String jiraUrl;

    // not db column
    private List<String> jiraProjects;

//    Проставляется относительно авторизованного пользователя
    private boolean isFavorite;

    public CaseObject() {

    }

    public CaseObject(Long id) {
        this.id = id;
    }

    public String defGUID () {
        En_CaseType t = type;
        return t != null ? t.makeGUID(this.caseNumber) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public En_CaseType getType() {
        return type;
    }

    public void setType(En_CaseType type) {
        this.type = type;
    }

    /**Используется в API
     * https://wiki.protei.ru/doku.php?id=protei:om:acs:portalv4_config
     * */
    public int getTypeId() {
        return type!=null?type.getId():0;
    }
    /**Используется в API
     * https://wiki.protei.ru/doku.php?id=protei:om:acs:portalv4_config
     * */
    public void setTypeId(int typeId) {
        type = En_CaseType.find( typeId );
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(Long caseNumber) {
        this.caseNumber = caseNumber;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
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

    public void setImportanceLevel(ImportanceLevel importanceLevel) {
        this.impLevel = importanceLevel == null ? null : importanceLevel.getId();
        this.importanceCode = importanceLevel == null ? null : importanceLevel.getCode();
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorIp() {
        return creatorIp;
    }

    public void setCreatorIp(String creatorIp) {
        this.creatorIp = creatorIp;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }

    public Long getInitiatorCompanyId() {
        return initiatorCompanyId;
    }

    public void setInitiatorCompanyId(Long initiatorCompanyId) {
        this.initiatorCompanyId = initiatorCompanyId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getCreatorInfo() {
        return creatorInfo;
    }

    public void setCreatorInfo(String creatorInfo) {
        this.creatorInfo = creatorInfo;
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

    public void setInitiatorCompany(Company company) {
        this.initiatorCompany = company;
        this.initiatorCompanyId = company == null ? null : company.getId();
    }

    public void setManager(PersonShortView person) {
        this.manager = person;
        this.managerId = person == null ? null : person.getId();
    }

    public void setInitiator(Person person) {
        this.initiator = person;
        this.initiatorId = person == null ? null : person.getId();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isPrivateCase() {
        return privateCase;
    }

    public void setPrivateCase(boolean privateCase) {
        this.privateCase = privateCase;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator (Person person) {
        this.creator = person;
        this.creatorId = person != null ? person.getId() : null;
    }

    public Person getInitiator() {
        return initiator;
    }

    public PersonShortView getManager() {
        return manager;
    }

    public Company getInitiatorCompany() {
        return initiatorCompany;
    }

    public List<Attachment> getAttachments() {
        return attachments == null? Collections.EMPTY_LIST: attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public boolean isAttachmentExists() {
        return isAttachmentExists;
    }

    public void setAttachmentExists(boolean attachmentExists) {
        isAttachmentExists = attachmentExists;
    }

    public List<CaseLocation> getLocations() {
        return locations;
    }

    public void setLocations( List<CaseLocation> locations ) {
        this.locations = locations;
    }

    public List< CaseMember > getMembers() {
        return members;
    }

    public void setMembers( List< CaseMember > members ) {
        this.members = members;
    }

    public String getExtAppType() {
        return extAppType;
    }

    public void setExtAppType(String extAppType) {
        this.extAppType = extAppType;
    }

    public Set<Person> getNotifiers() {
        return notifiers;
    }

    public void setNotifiers(Set<Person> notifiers) {
        this.notifiers = notifiers;
    }

    public Long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(Long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public En_TimeElapsedType getTimeElapsedType() {
        return timeElapsedType;
    }

    public void setTimeElapsedType( En_TimeElapsedType timeElapsedType ) {
        this.timeElapsedType = timeElapsedType;
    }

    public CaseObjectMetaJira getCaseObjectMetaJira() {
        return caseObjectMetaJira;
    }

    public void setCaseObjectMetaJira(CaseObjectMetaJira caseObjectMetaJira) {
        this.caseObjectMetaJira = caseObjectMetaJira;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getJiraUrl() {
        return jiraUrl;
    }

    public void setJiraUrl(String jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public List<String> getJiraProjects() {
        return jiraProjects;
    }

    public void setJiraProjects(List<String> jiraProjects) {
        this.jiraProjects = jiraProjects;
    }

    public EntityOption toEntityOption() {
        return new EntityOption(this.getName(), this.getId());
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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

    public String getImportanceCode() {
        return importanceCode;
    }

    @Override
    public String getAuditType() {
        return "CaseObject";
    }

    public interface Columns {
        String ID = "id";
        String EXT_APP = "EXT_APP";
        String CASE_TYPE = "case_type";
        String PAUSE_DATE = "pause_date";
        String DELETED = "deleted";
        String INFO = "INFO";
        String CASENO = "CASENO";
        String CREATED = "CREATED";
        String MODIFIED = "MODIFIED";
        String CREATOR = "CREATOR";
        String STATE = "STATE";
        String CASE_NAME = "CASE_NAME";
        String MANAGER = "MANAGER";
        String MANAGER_COMPANY_ID = "manager_company_id";
        String PLATFORM_ID = "platform_id";
        String AUTO_CLOSE = "auto_close";
        String DEADLINE = "deadline";
        String WORK_TRIGGER = "work_trigger";
        String INITIATOR_COMPANY = "initiator_company";
        String INITIATOR = "initiator";
        String IMPORTANCE = "IMPORTANCE";
        String PRODUCT_ID = "product_id";
    }

    public static final int NOT_DELETED = 0;

    @Override
    public String toString() {
        return "CaseObject{" +
                "id=" + id +
                ", type=" + type +
                ", caseNumber=" + caseNumber +
                ", created=" + created +
                ", modified=" + modified +
                ", name='" + name + '\'' +
                ", extId='" + extId + '\'' +
                ", info='" + info + '\'' +
                ", stateId=" + stateId +
                ", stateName='" + stateName + '\'' +
                ", stateColor='" + stateColor + '\'' +
                ", stateInfo='" + stateInfo + '\'' +
                ", impLevel=" + impLevel +
                ", importanceCode='" + importanceCode + '\'' +
                ", creatorId=" + creatorId +
                ", creator=" + creator +
                ", creatorIp='" + creatorIp + '\'' +
                ", initiatorId=" + initiatorId +
                ", initiator=" + initiator +
                ", initiatorCompanyId=" + initiatorCompanyId +
                ", initiatorCompany=" + initiatorCompany +
                ", productId=" + productId +
                ", product=" + product +
                ", managerId=" + managerId +
                ", manager=" + manager +
                ", keywords='" + keywords + '\'' +
                ", emails='" + emails + '\'' +
                ", creatorInfo='" + creatorInfo + '\'' +
                ", deleted=" + deleted +
                ", privateCase=" + privateCase +
                ", isAttachmentExists=" + isAttachmentExists +
                ", attachments=" + attachments +
                ", locations=" + locations +
                ", members=" + members +
                ", extAppType='" + extAppType + '\'' +
                ", notifiers=" + notifiers +
                ", timeElapsed=" + timeElapsed +
                ", platformId=" + platformId +
                ", platformName='" + platformName + '\'' +
                ", pauseDate=" + pauseDate +
                ", managerCompanyId=" + managerCompanyId +
                ", managerCompanyName='" + managerCompanyName + '\'' +
                ", plans=" + plans +
                ", autoClose=" + autoClose +
                ", deadline=" + deadline +
                ", workTrigger=" + workTrigger +
                ", timeElapsedType=" + timeElapsedType +
                ", caseObjectMetaJira=" + caseObjectMetaJira +
                ", jiraUrl='" + jiraUrl + '\'' +
                ", jiraProjects=" + jiraProjects +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
