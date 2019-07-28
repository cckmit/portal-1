package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_DocumentState;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DocumentType;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class DocumentQuery extends BaseQuery {
    private Set<En_OrganizationCode> organizationCodes;
    private Long managerId;
    private DocumentType documentType;
    private Date from, to;
    private List<String> keywords;
    private String inTextQuery;
    private List<Long> onlyIds;
    private Boolean isApproved;
    private List<String> decimalNumbers;
    private List<Long> equipmentIds;
    private Long projectId;
    private En_DocumentState state;

    public DocumentQuery() {
    }

    public DocumentQuery(String searchString, En_SortField sortField, En_SortDir sortDir, Set<En_OrganizationCode> organizationCodes, DocumentType documentType, Date from, Date to, List<String> keywords, Long managerId, String inTextQuery, Boolean isApproved, En_DocumentState state) {
        super(searchString, sortField, sortDir);
        this.organizationCodes = organizationCodes;
        this.from = from;
        this.to = to;
        this.keywords = keywords;
        this.documentType = documentType;
        this.managerId = managerId;
        this.inTextQuery = inTextQuery;
        this.isApproved = isApproved;
        this.state = state;
    }

    public En_DocumentState getState() { return state; }

    public void setState(En_DocumentState state) { this.state = state; }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Set<En_OrganizationCode> getOrganizationCodes() {
        return organizationCodes;
    }

    public void setOrganizationCodes(Set<En_OrganizationCode> organizationCodes) {
        this.organizationCodes = organizationCodes;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getInTextQuery() {
        return inTextQuery;
    }

    public void setInTextQuery(String inTextQuery) {
        this.inTextQuery = inTextQuery;
    }

    public List<Long> getOnlyIds() {
        return onlyIds;
    }

    public void setOnlyIds(List<Long> onlyIds) {
        this.onlyIds = onlyIds;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public List<String> getDecimalNumbers() {
        return decimalNumbers;
    }

    public void setDecimalNumbers(List<String> decimalNumbers) {
        this.decimalNumbers = decimalNumbers;
    }

    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
