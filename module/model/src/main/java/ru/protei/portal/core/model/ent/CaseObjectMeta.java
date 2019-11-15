package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.struct.JiraMetaData;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@JdbcEntity(table = "case_object")
public class CaseObjectMeta implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    @JdbcColumn(name = "MODIFIED")
    private Date modified;

    @JdbcColumn(name = "STATE")
    private long stateId;

    @JdbcColumn(name = "IMPORTANCE")
    private Integer impLevel;

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

    @JdbcManyToMany(linkTable = "case_notifier", localLinkColumn = "case_id", remoteLinkColumn = "person_id")
    private Set<Person> notifiers; //may contain partially filled objects!

    @JdbcColumn(name = "platform_id")
    private Long platformId;

    @JdbcJoinedColumn(localColumn = "platform_id", table = "platform", remoteColumn = "id", mappedColumn = "name")
    private String platformName;

    @JdbcColumn(name = "time_elapsed")
    private Long timeElapsed;

    // not db column
    private En_TimeElapsedType timeElapsedType;

    // not db column
    private JiraMetaData jiraMetaData;

    public CaseObjectMeta() {}

    public CaseObjectMeta(CaseObject caseObject) {
        fillFromCaseObject(caseObject);
    }

    public CaseObjectMeta fillFromCaseObject(CaseObject co) {
        setId(co.getId());
        setModified(co.getModified());
        setStateId(co.getStateId());
        setImpLevel(co.getImpLevel());
        setInitiator(co.getInitiator());
        setInitiatorId(co.getInitiatorId());
        setInitiatorCompany(co.getInitiatorCompany());
        setInitiatorCompanyId(co.getInitiatorCompanyId());
        setProduct(co.getProduct());
        setProductId(co.getProductId());
        setManager(co.getManager());
        setManagerId(co.getManagerId());
        setNotifiers(co.getNotifiers());
        setPlatformId(co.getPlatformId());
        setTimeElapsed(co.getTimeElapsed());
        setTimeElapsedType(co.getTimeElapsedType());
        setJiraMetaData(co.getJiraMetaData());
        return this;
    }

    public CaseObject collectToCaseObject(CaseObject co) {
        co.setId(getId());
        co.setModified(getModified());
        co.setStateId(getStateId());
        co.setImpLevel(getImpLevel());
        co.setInitiator(getInitiator());
        co.setInitiatorId(getInitiatorId());
        co.setInitiatorCompany(getInitiatorCompany());
        co.setInitiatorCompanyId(getInitiatorCompanyId());
        co.setProduct(getProduct());
        co.setProductId(getProductId());
        co.setManager(getManager());
        co.setManagerId(getManagerId());
        co.setNotifiers(getNotifiers());
        co.setPlatformId(getPlatformId());
        co.setTimeElapsed(getTimeElapsed());
        co.setTimeElapsedType(getTimeElapsedType());
        co.setJiraMetaData(getJiraMetaData());
        return co;
    }

    public Long getId() {
        return id;
    }
    
    private void setId(Long id) {
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

    public En_CaseState getState() {
        return En_CaseState.getById(getStateId());
    }

    public void setState(En_CaseState state) {
        setStateId(state.getId());
    }

    public Integer getImpLevel() {
        return impLevel;
    }

    public void setImpLevel(Integer impLevel) {
        this.impLevel = impLevel;
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

    public Person getManager() {
        return manager;
    }

    public void setManager(Person manager) {
        this.manager = manager;
        this.managerId = manager == null ? null : manager.getId();
    }

    public Set<Person> getNotifiers() {
        return notifiers;
    }

    public void setNotifiers(Set<Person> notifiers) {
        this.notifiers = notifiers;
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

    public Long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(Long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public En_TimeElapsedType getTimeElapsedType() {
        return timeElapsedType;
    }

    public void setTimeElapsedType(En_TimeElapsedType timeElapsedType) {
        this.timeElapsedType = timeElapsedType;
    }

    public JiraMetaData getJiraMetaData() {
        return jiraMetaData;
    }

    public void setJiraMetaData(JiraMetaData jiraMetaData) {
        this.jiraMetaData = jiraMetaData;
    }
}
