package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

/**
 * Created by michael on 19.05.16.
 */
@JdbcEntity(table = "case_state_matrix")
public class CaseStateMatrix {

    @JdbcId(name="id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="CASE_TYPE")
    private Long caseTypeId;

    @JdbcColumn(name="CASE_STATE")
    private Long caseStateId;

    @JdbcColumn(name="view_order")
    private int viewOrder;

    @JdbcColumn(name="OLD_ID")
    private Long oldId;

    @JdbcColumn(name="OLD_CODE")
    private String oldCode;

    @JdbcColumn(name="info")
    private String info;


    public CaseStateMatrix() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCaseTypeId() {
        return caseTypeId;
    }

    public void setCaseTypeId(Long caseTypeId) {
        this.caseTypeId = caseTypeId;
    }

    public Long getCaseStateId() {
        return caseStateId;
    }

    public void setCaseStateId(Long caseStateId) {
        this.caseStateId = caseStateId;
    }

    public int getViewOrder() {
        return viewOrder;
    }

    public void setViewOrder(int viewOrder) {
        this.viewOrder = viewOrder;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
