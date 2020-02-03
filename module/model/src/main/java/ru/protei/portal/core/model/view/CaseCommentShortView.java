package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by michael on 19.05.16.
 */
@JdbcEntity(table = "case_comment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseCommentShortView {

    @JdbcId(name="id" , idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcJoinedColumn( mappedColumn = "caseno", table = "case_object", localColumn = "CASE_ID", remoteColumn = "ID" )
    private Long caseNumber;

    @JdbcJoinedColumn( mappedColumn = "displayShortName", table = "Person", localColumn = "author_id", remoteColumn = "ID" )
    private String author;

    @JdbcJoinedColumn( mappedColumn = "cname", joinPath = {
            @JdbcJoinPath( table = "Person", localColumn = "author_id", remoteColumn = "id" ),
            @JdbcJoinPath( table = "Company", localColumn = "company_id", remoteColumn = "id" )
    })
    private String authorCompanyName;

    @JdbcJoinedColumn( mappedColumn = "category_name", joinPath = {
            @JdbcJoinPath( table = "Person", localColumn = "author_id", remoteColumn = "id" ),
            @JdbcJoinPath( table = "Company", localColumn = "company_id", remoteColumn = "id" ),
            @JdbcJoinPath( table = "Company_category", localColumn = "category_id", remoteColumn = "id" )
    })
    private String authorCompanyCategory;

    @JdbcColumn(name="cstate_id")
    private Long caseStateId;

    @JdbcColumn(name="cimp_level")
    private Integer caseImpLevel;

    @JdbcColumn(name="reply_to")
    private Long replyTo;

    @JdbcColumn(name="comment_text")
    private String text;

    @JdbcOneToMany(table = "case_attachment", remoteColumn = "ccomment_id", additionalConditions = @JdbcManyJoinData(remoteColumn="case_id", localColumn = "case_id"))
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CaseAttachment> caseAttachments;

    @JdbcColumn(name="time_elapsed")
    private Long timeElapsed;

    @JdbcColumn(name="time_elapsed_type")
    @JdbcEnumerated(EnumType.ID)
    private En_TimeElapsedType timeElapsedType;

    @JdbcColumn(name = "private_flag")
    private boolean privateComment;

    // not db column
    private Date updated;

    // not db column
    private boolean deleted;

    public CaseCommentShortView() {}

    public CaseCommentShortView(String text) {
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

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(Long caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorCompanyName() {
        return authorCompanyName;
    }

    public void setAuthorCompanyName(String authorCompanyName) {
        this.authorCompanyName = authorCompanyName;
    }

    public String getAuthorCompanyCategory() {
        return authorCompanyCategory;
    }

    public void setAuthorCompanyCategory(String authorCompanyCategory) {
        this.authorCompanyCategory = authorCompanyCategory;
    }

    public Long getCaseStateId() {
        return caseStateId;
    }

    public void setCaseStateId(Long caseStateId) {
        this.caseStateId = caseStateId;
    }

    public Integer getCaseImpLevel() {
        return caseImpLevel;
    }

    public void setCaseImpLevel(Integer caseImpLevel) {
        this.caseImpLevel = caseImpLevel;
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

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseCommentShortView that = (CaseCommentShortView) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id );
    }

    @Override
    public String toString() {
        return "CaseCommentShortView{" +
                "id=" + id +
                ", created=" + created +
                ", caseNumber=" + caseNumber +
                ", author='" + author + '\'' +
                ", authorCompanyName='" + authorCompanyName + '\'' +
                ", authorCompanyCategory='" + authorCompanyCategory + '\'' +
                ", caseStateId=" + caseStateId +
                ", caseImpLevel=" + caseImpLevel +
                ", replyTo=" + replyTo +
                ", text='" + text + '\'' +
                ", caseAttachments=" + caseAttachments +
                ", timeElapsed=" + timeElapsed +
                ", timeElapsedType=" + timeElapsedType +
                ", privateComment=" + privateComment +
                ", updated=" + updated +
                ", deleted=" + deleted +
                '}';
    }
}
