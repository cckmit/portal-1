package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Objects;

@JdbcEntity(table = "plan_to_case_object")
public class PlanToCaseObject implements Serializable{

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "plan_id")
    private Long planId;

    @JdbcColumn(name = "case_object_id")
    private Long caseObjectId;

    @JdbcColumn(name = "order_number")
    private Integer orderNumber;

    public PlanToCaseObject() {}

    public PlanToCaseObject(Long planId, Long caseObjectId) {
        this.planId = planId;
        this.caseObjectId = caseObjectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getCaseObjectId() {
        return caseObjectId;
    }

    public void setCaseObjectId(Long caseObjectId) {
        this.caseObjectId = caseObjectId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        return "PlanToCaseObject{" +
                "id=" + id +
                ", planId=" + planId +
                ", caseObjectId=" + caseObjectId +
                ", orderNumber=" + orderNumber +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanToCaseObject that = (PlanToCaseObject) o;
        return Objects.equals(planId, that.planId) &&
                Objects.equals(caseObjectId, that.caseObjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId, caseObjectId);
    }
}
