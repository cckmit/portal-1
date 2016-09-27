package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;

/**
 * Created by michael on 20.05.16.
 */
@JdbcEntity(table = "case_term")
public class CaseTerm {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "CASE_ID")
    private Long caseId;

    @JdbcColumn(name = "CREATED")
    private Date created;

    @JdbcColumn(name = "CREATOR_ID")
    private Long creatorId;

    @JdbcColumn(name = "TERM_TYPE")
    private int termTypeId;

    @JdbcColumn(name = "TORDER")
    private int termOrder;

    @JdbcColumn(name = "END_TIME")
    private Date endTime;

    @JdbcColumn(name = "TLABEL")
    private String labelText;

    @JdbcColumn(name = "STAGE_ID")
    private Long stageId;

    @JdbcColumn(name = "old_id")
    private Long oldId;

    public CaseTerm() {
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public int getTermTypeId() {
        return termTypeId;
    }

    public void setTermTypeId(int termTypeId) {
        this.termTypeId = termTypeId;
    }

    public int getTermOrder() {
        return termOrder;
    }

    public void setTermOrder(int termOrder) {
        this.termOrder = termOrder;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }
}
