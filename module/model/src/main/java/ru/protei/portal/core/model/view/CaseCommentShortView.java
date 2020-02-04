package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Objects;

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

    @JdbcColumn(name="cstate_id")
    private Long caseStateId;

    @JdbcColumn(name="cimp_level")
    private Integer caseImpLevel;

    @JdbcColumn(name="reply_to")
    private Long replyTo;

    @JdbcColumn(name="comment_text")
    private String text;

    @JdbcColumn(name="time_elapsed")
    private Long timeElapsed;

    @JdbcColumn(name = "private_flag")
    private boolean privateComment;

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

    public Long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(Long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }


    public boolean isPrivateComment() {
        return privateComment;
    }

    public void setPrivateComment(boolean privateComment) {
        this.privateComment = privateComment;
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
                ", caseStateId=" + caseStateId +
                ", caseImpLevel=" + caseImpLevel +
                ", replyTo=" + replyTo +
                ", text='" + text + '\'' +
                ", timeElapsed=" + timeElapsed +
                ", privateComment=" + privateComment +
                '}';
    }
}
