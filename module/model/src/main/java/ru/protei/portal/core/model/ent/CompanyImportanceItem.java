package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "company_importance_item")
public class CompanyImportanceItem implements Serializable {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "company_id")
    private Long companyId;

    @JdbcColumn(name = "importance_level_id")
    private Integer importanceLevelId;

    @JdbcColumn(name = "order_number")
    private Integer orderNumber;

    @JdbcJoinedColumn(localColumn = "importance_level_id", remoteColumn = "id", table = "importance_level", mappedColumn = "code")
    private String importanceCode;

    public CompanyImportanceItem() {}

    public CompanyImportanceItem(Long companyId, Integer importanceLevelId, Integer orderNumber) {
        this.companyId = companyId;
        this.importanceLevelId = importanceLevelId;
        this.orderNumber = orderNumber;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getImportanceLevelId() {
        return importanceLevelId;
    }

    public void setImportanceLevelId(Integer importanceLevelId) {
        this.importanceLevelId = importanceLevelId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getImportanceCode() {
        return importanceCode;
    }

    @Override
    public String toString() {
        return "CompanyImportanceItem{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", importanceLevelId=" + importanceLevelId +
                ", orderNumber=" + orderNumber +
                ", importanceCode='" + importanceCode + '\'' +
                '}';
    }
}
