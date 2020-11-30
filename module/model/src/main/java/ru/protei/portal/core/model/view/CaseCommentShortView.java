package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
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

    @JdbcColumn(name = "privacy_type")
    private En_CaseCommentPrivacyType privacyType;

    @JdbcColumn(name="case_id")
    private Long caseId;

    @JdbcColumn(name="author_id")
    private Long authorId;

    @JdbcJoinedColumn( mappedColumn = "displayShortName", table = "person", localColumn = "author_id", remoteColumn = "ID" )
    private String authorName;

    @JdbcJoinedColumn( mappedColumn = "company_id", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "author_id", remoteColumn = "id" ),
    })
    private Long companyId;

    @JdbcJoinedColumn( mappedColumn = "cname", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "author_id", remoteColumn = "id" ),
            @JdbcJoinPath( table = "company", localColumn = "company_id", remoteColumn = "id" )
    })
    private String companyName;

    @JdbcJoinedColumn( mappedColumn = "category_id", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "author_id", remoteColumn = "id" ),
            @JdbcJoinPath( table = "company", localColumn = "company_id", remoteColumn = "id" ),
    })
    private Integer companyCategoryId;

    @JdbcColumn(name="cstate_id")
    private Integer caseStateId;

    @JdbcColumn(name="cimp_level")
    private Integer caseImpLevel;

    @JdbcColumn(name="comment_text")
    private String text;

    public CaseCommentShortView() {}

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

    public boolean isPrivateComment() {
        return privacyType == En_CaseCommentPrivacyType.PRIVATE;
    }

    public void setPrivateComment(boolean privateComment) {
        privacyType = privateComment ? En_CaseCommentPrivacyType.PRIVATE : En_CaseCommentPrivacyType.PUBLIC;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getCompanyCategoryId() {
        return companyCategoryId;
    }

    public void setCompanyCategoryId(Integer companyCategoryId) {
        this.companyCategoryId = companyCategoryId;
    }

    public Integer getCaseStateId() {
        return caseStateId;
    }

    public void setCaseStateId(Integer caseStateId) {
        this.caseStateId = caseStateId;
    }

    public Integer getCaseImpLevel() {
        return caseImpLevel;
    }

    public void setCaseImpLevel(Integer caseImpLevel) {
        this.caseImpLevel = caseImpLevel;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
                ", privacyType=" + privacyType +
                ", caseId=" + caseId +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", companyCategoryId=" + companyCategoryId +
                ", caseStateId=" + caseStateId +
                ", caseImpLevel=" + caseImpLevel +
                ", text='" + text + '\'' +
                '}';
    }
}
