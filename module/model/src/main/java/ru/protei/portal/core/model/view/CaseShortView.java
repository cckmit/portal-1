package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Сокращенное представление кейса
 */
@JdbcEntity(table = "case_object")
public class CaseShortView implements Serializable {

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

    @JdbcColumn(name = "INFO")
    private String info;

    @JdbcColumn(name = "STATE")
    private long stateId;

    @JdbcColumn(name = "IMPORTANCE")
    private Integer impLevel;

    @JdbcColumn(name = "private_flag")
    private boolean privateCase;

    @JdbcColumn(name = "INITIATOR")
    private Long initiatorId;

    // Вариант 1: mappedColumn + table + localColumn + remoteColumn + опционально sqlTableAlias
    @JdbcJoinedColumn( mappedColumn = "displayname", table = "Person", localColumn = "INITIATOR", remoteColumn = "ID" )
    private String initiatorName;

    @JdbcJoinedColumn( table = "Person", localColumn = "INITIATOR", remoteColumn = "ID", mappedColumn = "displayShortName")
    private String initiatorShortName;

    @JdbcColumn(name = "initiator_company")
    private Long initiatorCompanyId;

    @JdbcJoinedColumn( table="Company", localColumn = "initiator_company", remoteColumn = "id", mappedColumn = "cname")
    private String initiatorCompanyName;

    @JdbcColumn(name = "product_id")
    private Long productId;

    @JdbcJoinedColumn(table = "dev_unit", localColumn = "product_id", remoteColumn = "id", mappedColumn = "UNIT_NAME")
    private String productName;

    @JdbcColumn(name = "MANAGER")
    private Long managerId;

    @JdbcJoinedColumn( table = "person", localColumn = "MANAGER", remoteColumn = "id", mappedColumn = "displayname" )
    private String managerName;

    @JdbcJoinedColumn( table = "Person", localColumn = "MANAGER", remoteColumn = "ID", mappedColumn = "displayShortName")
    private String managerShortName;

    @JdbcJoinedColumn( mappedColumn = "cname", joinPath = {
            @JdbcJoinPath( table = "Person", localColumn = "MANAGER", remoteColumn = "id" ),
            @JdbcJoinPath( table = "Company", localColumn = "company_id", remoteColumn = "id" )
    })
    private String managerCompanyName;

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

    public String getInfo() {
        return info;
    }

    public void setInfo( String info ) {
        this.info = info;
    }

    public long getStateId() {
        return stateId;
    }

    public void setStateId( long stateId ) {
        this.stateId = stateId;
    }

    public Integer getImpLevel() {
        return impLevel;
    }

    public void setImpLevel( Integer impLevel ) {
        this.impLevel = impLevel;
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

    @Override
    public String toString() {
        return "CaseShortView{" +
                "caseNumber=" + caseNumber +
                ", id=" + id +
                ", typeId=" + typeId +
                ", created=" + created +
                ", modified=" + modified +
                ", info='" + info + '\'' +
                ", stateId=" + stateId +
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
                ", managerCompanyName='" + managerCompanyName + '\'' +
                '}';
    }
}
