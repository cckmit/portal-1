package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Objects;

@JdbcEntity(table = "user_dashboard")
public class UserDashboard implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "login_id")
    private Long loginId;

    @JdbcColumn(name = "case_filter_id")
    private Long caseFilterId;

    @JdbcColumn(name = "name")
    private String name;

    @JdbcColumn(name = "is_collapsed")
    private Boolean isCollapsed;

    @JdbcColumn(name = "order_number")
    private Integer orderNumber;

    private CaseFilterDto<CaseQuery> caseFilterDto;

    public UserDashboard() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public Long getCaseFilterId() {
        return caseFilterId;
    }

    public void setCaseFilterId(Long caseFilterId) {
        this.caseFilterId = caseFilterId;
        if (caseFilterDto != null &&
                caseFilterDto.getCaseFilter() != null &&
                !Objects.equals(caseFilterDto.getCaseFilter().getId(), caseFilterId)) {

            CaseFilter caseFilter = new CaseFilter();
            caseFilter.setId(caseFilterId);

            this.caseFilterDto.setCaseFilter(caseFilter);
        }
    }

    public CaseFilter getCaseFilter() {
        return caseFilterDto == null ? null : caseFilterDto.getCaseFilter();
    }

    public void setCaseFilter(CaseFilter caseFilter) {
        if (this.caseFilterDto != null) {
            this.caseFilterDto.setCaseFilter(caseFilter);
        }

        this.caseFilterId = caseFilter == null ? null : caseFilter.getId();
    }

    public CaseQuery getCaseQuery() {
        return caseFilterDto == null ? null : caseFilterDto.getQuery();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCollapsed() {
        return isCollapsed;
    }

    public void setCollapsed(Boolean isCollapsed) {
        this.isCollapsed = isCollapsed;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public CaseFilterDto<CaseQuery> getCaseFilterDto() {
        return caseFilterDto;
    }

    public void setCaseFilterDto(CaseFilterDto<CaseQuery> caseFilterDto) {
        this.caseFilterDto = caseFilterDto;
    }

    @Override
    public String toString() {
        return "UserDashboard{" +
                "id=" + id +
                ", loginId=" + loginId +
                ", caseFilterId=" + caseFilterId +
                ", name='" + name + '\'' +
                ", isCollapsed=" + isCollapsed +
                ", orderNumber=" + orderNumber +
                ", caseFilterDto=" + caseFilterDto +
                '}';
    }
}
