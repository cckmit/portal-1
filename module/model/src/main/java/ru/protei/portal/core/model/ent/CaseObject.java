package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by michael on 19.05.16.
 */
@JdbcEntity(table = "case_object")
public class CaseObject extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "case_type")
    private int typeId;

    @JdbcColumn(name = "CASENO")
    private Long caseNumber;

    @JdbcColumn(name = "CREATED")
    private Date created;

    @JdbcColumn(name = "MODIFIED")
    private Date modified;

    @JdbcColumn(name = "CASE_NAME")
    private String name;

    @JdbcColumn(name = "EXT_ID")
    private String extId;

    @JdbcColumn(name = "INFO")
    private String info;

    @JdbcColumn(name = "STATE")
    private long stateId;

    @JdbcColumn(name = "IMPORTANCE")
    private Integer impLevel;

    @JdbcColumn(name = "CREATOR")
    private Long creatorId;

    @JdbcJoinedObject( localColumn = "CREATOR", remoteColumn = "id", updateLocalColumn = false )
    private Person creator;

    @JdbcColumn(name = "CREATOR_IP")
    private String creatorIp;

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
    private Person manager;

    @JdbcColumn(name = "KEYWORDS")
    private String keywords;

    @JdbcColumn(name = "ISLOCAL")
    private int local;

    @JdbcColumn(name = "EMAILS")
    private String emails;

    @JdbcColumn(name = "creator_info")
    private String creatorInfo;

    @JdbcColumn(name = "deleted")
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

    @JdbcColumn(name = "EXT_APP")
    private String extAppType;

    @JdbcManyToMany(linkTable = "case_notifier", localLinkColumn = "case_id", remoteLinkColumn = "person_id")
    private Set<Person> notifiers; //may contain partially filled objects!

    @JdbcColumn(name = "time_elapsed")
    private Long timeElapsed;

    @JdbcManyToMany(linkTable = "project_to_product", localLinkColumn = "project_id", remoteLinkColumn = "product_id")
    private Set<DevUnit> products;

    @JdbcManyToMany(linkTable = "case_object_tag", localLinkColumn = "case_id", remoteLinkColumn = "tag_id")
    private Set<CaseTag> tags;

    @JdbcColumn(name = "platform_id")
    private Long platformId;

    @JdbcJoinedColumn(localColumn = "platform_id", table = "platform", remoteColumn = "id", mappedColumn = "name")
    private String platformName;

    // not db column
    private List<CaseLink> links;

    // not db column
    private En_TimeElapsedType timeElapsedType;

    public CaseObject() {

    }

    public CaseObject(Long id) {
        this.id = id;
    }

    public String defGUID () {
        En_CaseType t = En_CaseType.find(this.typeId);
        return t != null ? t.makeGUID(this.caseNumber) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
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

    public Integer getImpLevel() {
        return impLevel;
    }

    public void setImpLevel(Integer impLevel) {
        this.impLevel = impLevel;
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

    public int getLocal() {
        return local;
    }

    public void setLocal(int local) {
        this.local = local;
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

    public void setManager(Person person) {
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

    public Person getManager() {
        return manager;
    }

    public Company getInitiatorCompany() {
        return initiatorCompany;
    }

    public En_CaseType getCaseType () {
        return En_CaseType.find(this.typeId);
    }

    public void setCaseType (En_CaseType type) {
        this.typeId = type.getId();
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

    public En_CaseState getState () {
        return En_CaseState.getById(this.stateId);
    }

    public void setState (En_CaseState state) {
        this.stateId = state.getId();
    }


    public En_ImportanceLevel importanceLevel () {
        return En_ImportanceLevel.getById(this.impLevel);
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

    public List<CaseLink> getLinks() {
        return links;
    }

    public void setLinks(List<CaseLink> links) {
        this.links = links;
    }

    public Set<DevUnit> getProducts() {
        return products;
    }

    public void setProducts(Set<DevUnit> products) {
        this.products = products;
    }

    public En_TimeElapsedType getTimeElapsedType() {
        return timeElapsedType;
    }

    public void setTimeElapsedType( En_TimeElapsedType timeElapsedType ) {
        this.timeElapsedType = timeElapsedType;
    }

    public Set<CaseTag> getTags() {
        return tags;
    }

    public void setTags(Set<CaseTag> tags) {
        this.tags = tags;
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

    @Override
    public String getAuditType() {
        return "CaseObject";
    }

    @Override
    public String toString() {
        return "CaseObject{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", caseNumber=" + caseNumber +
                ", created=" + created +
                ", modified=" + modified +
                ", name='" + name + '\'' +
                ", extId='" + extId + '\'' +
                ", info='" + info + '\'' +
                ", stateId=" + stateId +
                ", impLevel=" + impLevel +
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
                ", local=" + local +
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
                ", products=" + products +
                ", tags=" + tags +
                ", platformId=" + platformId +
                ", platformName=" + platformName +
                ", links=" + links +
                ", timeElapsedType=" + timeElapsedType +
                '}';
    }
}
