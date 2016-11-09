package ru.protei.portal.core.model.ent;

import protei.sql.Column;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

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

    @JdbcJoinedObject( localColumn = "INITIATOR", remoteColumn = "id", updateLocalColumn = false )
    private Person initiator;

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

    @Column(name = "creator_info")
    private String creatorInfo;


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

    public Person getCreator() {
        return creator;
    }

    public Person getInitiator() {
        return initiator;
    }

    public Person getManager() {
        return manager;
    }
}
