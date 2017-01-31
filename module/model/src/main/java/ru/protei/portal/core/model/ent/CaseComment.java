package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by michael on 19.05.16.
 */
@JdbcEntity(table = "case_comment")
public class CaseComment implements Serializable{

    @JdbcId(name="id" , idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcColumn(name="client_ip")
    private String clientIp;

    @JdbcColumn(name="case_id")
    private Long caseId;

    @JdbcJoinedObject(localColumn = "author_id", remoteColumn = "id", updateLocalColumn = true )
    private Person author;

    @JdbcColumn(name="cstate_id")
    private Long caseStateId;

    @JdbcColumn(name="reply_to")
    private Long replyTo;

    @JdbcColumn(name="vroom")
    private Long vroomId;

    @JdbcColumn(name="comment_text")
    private String text;

    @JdbcColumn(name="old_id")
    private Long oldId;

    private List<Long> attachmentsIds;

    public CaseComment() {}

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

    public Long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Long replyTo) {
        this.replyTo = replyTo;
    }

    public Long getVroomId() {
        return vroomId;
    }

    public void setVroomId(Long vroomId) {
        this.vroomId = vroomId;
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

    public List<Long> getAttachmentsIds() {
        return attachmentsIds;
    }

    public void setAttachmentsIds(List<Long> attachmentsIds) {
        this.attachmentsIds = attachmentsIds;
    }

    public void setAttachments(Collection<Attachment> attachments){
        if(attachments == null)
            return;

        this.attachmentsIds = attachments
                .stream()
                .map(Attachment::getId)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "CaseComment{" +
                "id=" + id +
                ", created=" + created +
                ", clientIp='" + clientIp + '\'' +
                ", caseId=" + caseId +
                ", authorId=" + author +
                ", caseStateId=" + caseStateId +
                ", replyTo=" + replyTo +
                ", vroomId=" + vroomId +
                ", text='" + text + '\'' +
                ", oldId=" + oldId +
                '}';
    }
}
