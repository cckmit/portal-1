package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by michael on 19.05.16.
 */
@JdbcEntity(table = "case_object")
public class CaseObject implements Serializable {

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

    @JdbcManyToMany(linkTable = "case_location", localLinkColumn = "CASE_ID", remoteLinkColumn = "LOCATION_ID" )
    private List<Location> locations;

    @JdbcOneToMany( table = "case_member", localColumn = "id", remoteColumn = "CASE_ID" )
    private List<CaseMember> members;

    public CaseObject() {

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

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations( List<Location> locations ) {
        this.locations = locations;
    }

    public List< CaseMember > getMembers() {
        return members;
    }

    public void setMembers( List< CaseMember > members ) {
        this.members = members;
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
            ", locations=" + locations +
            ", members=" + members +
            '}';
    }
}
