package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by michael on 19.05.16.
 */
@JdbcEntity(table = "case_comment")
public class CaseComment extends AuditableObject {

    @JdbcId(name="id" , idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcColumn(name="client_ip")
    private String clientIp;

    @JdbcColumn(name="case_id")
    private Long caseId;

    @JdbcJoinedObject(localColumn = "author_id", remoteColumn = "id", updateLocalColumn = true, sqlTableAlias = "person")
    private Person author;

    @JdbcColumn(name="cstate_id")
    private Long caseStateId;

    @JdbcJoinedColumn(localColumn = "cstate_id", table = "case_state", remoteColumn = "id", mappedColumn = "STATE")
    private String caseStateName;

    @JdbcColumn(name="cimp_level")
    private Integer caseImpLevel;

    @JdbcColumn(name="cmanager_id")
    private Long caseManagerId;

    @JdbcJoinedColumn(localColumn = "cmanager_id", table = "person", remoteColumn = "ID", mappedColumn = "displayShortName")
    private String caseManagerShortName;

    @JdbcColumn(name="reply_to")
    private Long replyTo;

    @JdbcColumn(name="comment_text")
    private String text;

    @JdbcColumn(name="old_id")
    private Long oldId;

    @JdbcOneToMany(table = "case_attachment", remoteColumn = "ccomment_id", additionalConditions = @JdbcManyJoinData(remoteColumn="case_id", localColumn = "case_id"))
    private List<CaseAttachment> caseAttachments;

    @JdbcColumn(name="time_elapsed")
    private Long timeElapsed;

    @JdbcColumn(name="time_elapsed_type")
    @JdbcEnumerated(EnumType.ID)
    private En_TimeElapsedType timeElapsedType;

    @JdbcColumn(name = "remote_id")
    private String remoteId;

    @JdbcColumn(name = "remote_link_id")
    private Long remoteLinkId;

    @JdbcJoinedObject(localColumn = "remote_link_id", remoteColumn = "id", table = "case_link", sqlTableAlias = "case_link")
    private CaseLink remoteLink;

    @JdbcColumn(name = "original_author_name")
    private String originalAuthorName;

    @JdbcColumn(name = "original_author_full_name")
    private String originalAuthorFullName;

    @JdbcColumn(name = "private_flag")
    private boolean privateComment;

    @JdbcJoinedColumn(mappedColumn = "cname", joinPath = {
            @JdbcJoinPath(localColumn = "cmanager_id", remoteColumn = "id", table = "person"),
            @JdbcJoinPath(localColumn = "company_id", remoteColumn = "id", table = "company")
    })
    private String managerCompanyName;

    // not db column
    private Date updated;

    // not db column
    private boolean deleted;

    public CaseComment() {}

    public CaseComment(String text) {
        this.text = text;
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

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor( Person author ) {
        this.author = author;
    }

    public Long getAuthorId() {
        return author == null ? null : author.getId();
    }

    public void setAuthorId(Long authorId) {
        if ( author == null ) {
            author = new Person();
        }
        this.author.setId( authorId );
    }

    public Long getCaseStateId() {
        return caseStateId;
    }

    public void setCaseStateId(Long caseStateId) {
        this.caseStateId = caseStateId;
    }

    public String getCaseStateName() {
        return caseStateName;
    }

    public void setCaseStateName(String caseStateName) {
        this.caseStateName = caseStateName;
    }

    public Integer getCaseImpLevel() {
        return caseImpLevel;
    }

    public void setCaseImpLevel(Integer caseImpLevel) {
        this.caseImpLevel = caseImpLevel;
    }

    public En_ImportanceLevel getCaseImportance() {
        return En_ImportanceLevel.getById(this.caseImpLevel);
    }

    public void setCaseImportance( En_ImportanceLevel caseImportance ) {
        if(caseImportance == null) caseImpLevel = null;
        this.caseImpLevel = caseImportance.getId();
    }

    public Long getCaseManagerId() {
        return caseManagerId;
    }

    public void setCaseManagerId(Long caseManagerId) {
        this.caseManagerId = caseManagerId;
    }

    public String getCaseManagerShortName() {
        return caseManagerShortName;
    }

    public void setCaseManagerShortName(String caseManagerShortName) {
        this.caseManagerShortName = caseManagerShortName;
    }

    public Long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Long replyTo) {
        this.replyTo = replyTo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public List<CaseAttachment> getCaseAttachments() {
        return caseAttachments;
    }

    public void setCaseAttachments(List<CaseAttachment> attachments) {
        this.caseAttachments = attachments;
    }

    public Long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(Long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public Long getRemoteLinkId() {
        return remoteLinkId;
    }

    public void setRemoteLinkId(Long remoteLinkId) {
        this.remoteLinkId = remoteLinkId;
    }

    public CaseLink getRemoteLink() {
        return remoteLink;
    }

    public void setRemoteLink(CaseLink remoteLink) {
        this.remoteLink = remoteLink;
    }

    public String getOriginalAuthorName() {
        return originalAuthorName;
    }

    public void setOriginalAuthorName(String originalAuthorName) {
        this.originalAuthorName = originalAuthorName;
    }

    public String getOriginalAuthorFullName() {
        return originalAuthorFullName;
    }

    public void setOriginalAuthorFullName(String originalAuthorFullName) {
        this.originalAuthorFullName = originalAuthorFullName;
    }

    public En_TimeElapsedType getTimeElapsedType() {
        return timeElapsedType;
    }

    public void setTimeElapsedType( En_TimeElapsedType timeElapsedType ) {
        this.timeElapsedType = timeElapsedType;
    }

    public boolean isPrivateComment() {
        return privateComment;
    }

    public void setPrivateComment(boolean privateComment) {
        this.privateComment = privateComment;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getManagerCompanyName() {
        return managerCompanyName;
    }

    public void setManagerCompanyName(String managerCompanyName) {
        this.managerCompanyName = managerCompanyName;
    }

    @Override
    public String getAuditType() {
        return "CaseComment";
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseComment that = (CaseComment) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id );
    }

    @Override
    public String toString() {
        return "CaseComment{" +
                "id=" + id +
                ", created=" + created +
                ", clientIp='" + clientIp + '\'' +
                ", caseId=" + caseId +
                ", author=" + author +
                ", caseStateId=" + caseStateId +
                ", caseStateName=" + caseStateName +
                ", caseImpLevel=" + caseImpLevel +
                ", caseManagerId=" + caseManagerId +
                ", caseManagerShortName='" + caseManagerShortName + '\'' +
                ", replyTo=" + replyTo +
                ", text='" + text + '\'' +
                ", oldId=" + oldId +
                ", caseAttachments=" + caseAttachments +
                ", timeElapsed=" + timeElapsed +
                ", timeElapsedType=" + timeElapsedType +
                ", remoteId='" + remoteId + '\'' +
                ", remoteLinkId=" + remoteLinkId +
                ", remoteLink=" + remoteLink +
                ", originalAuthorName='" + originalAuthorName + '\'' +
                ", originalAuthorFullName='" + originalAuthorFullName + '\'' +
                ", privateComment=" + privateComment +
                ", managerCompanyName='" + managerCompanyName + '\'' +
                ", updated=" + updated +
                ", deleted=" + deleted +
                '}';
    }
}
