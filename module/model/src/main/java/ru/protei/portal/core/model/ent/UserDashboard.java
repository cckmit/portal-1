package ru.protei.portal.core.model.ent;

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

    @JdbcJoinedObject(localColumn = "case_filter_id", remoteColumn = "id")
    private CaseFilter caseFilter;

    @JdbcColumn(name = "name")
    private String name;

    @JdbcColumn(name = "is_collapsed")
    private Boolean isCollapsed;

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
        if (caseFilter != null && !Objects.equals(caseFilter.getId(), caseFilterId)) {
            this.caseFilter = new CaseFilter();
            this.caseFilter.setId(caseFilterId);
        }
    }

    public CaseFilter getCaseFilter() {
        return caseFilter;
    }

    public void setCaseFilter(CaseFilter caseFilter) {
        this.caseFilter = caseFilter;
        this.caseFilterId = caseFilter == null ? null : caseFilter.getId();
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

    @Override
    public String toString() {
        return "UserDashboard{" +
                "id=" + id +
                ", loginId=" + loginId +
                ", caseFilterId=" + caseFilterId +
                ", caseFilter=" + caseFilter +
                ", name='" + name + '\'' +
                ", isCollapsed=" + isCollapsed +
                '}';
    }
}
