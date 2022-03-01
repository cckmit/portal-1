package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.HasFilterQueryIds;
import ru.protei.portal.core.model.query.ProjectQuery;
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

    private CaseFilterDto<ProjectQuery> projectFilterDto;

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

    public void setProjectFilterId(Long projectFilterId) {
        if (caseFilterId != null) return;
        this.caseFilterId = projectFilterId;
        if (projectFilterDto != null &&
                projectFilterDto.getCaseFilter() != null &&
                !Objects.equals(projectFilterDto.getCaseFilter().getId(), projectFilterId)) {

            CaseFilter caseFilter = new CaseFilter();
            caseFilter.setId(projectFilterId);

            this.projectFilterDto.setCaseFilter(caseFilter);
        }
    }

    public CaseFilter getCaseFilter() {
        return caseFilterDto == null ? null : caseFilterDto.getCaseFilter();
    }

    public CaseFilter getProjectFilter() {
        return projectFilterDto == null ? null : projectFilterDto.getCaseFilter();
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

    public ProjectQuery getProjectQuery() {
        return projectFilterDto == null ? null : projectFilterDto.getQuery();
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

    public CaseFilterDto<ProjectQuery> getProjectFilterDto() {
        return projectFilterDto;
    }

    public void setCaseFilterDto(CaseFilterDto<CaseQuery> caseFilterDto) {
        this.caseFilterDto = caseFilterDto;
    }

    public void setProjectFilterDto(CaseFilterDto<ProjectQuery> projectFilterDto) {
        this.projectFilterDto = projectFilterDto;
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
