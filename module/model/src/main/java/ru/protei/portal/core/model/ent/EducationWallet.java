package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;

@JdbcEntity(table = "education_wallet")
public class EducationWallet implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="dep_id")
    private Long departmentId;

    @JdbcColumn(name="coins")
    private Integer coins;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "dep_id", remoteColumn = "id", table = "company_dep"),
            @JdbcJoinPath(localColumn = "company_id", remoteColumn = "id", table = "company"),
    }, mappedColumn = "cname")
    private String companyName;

    @JdbcJoinedColumn(localColumn = "dep_id", table = "company_dep", remoteColumn = "id", mappedColumn = "dep_name")
    private String departmentName;

    // not db column
    private List<EducationEntry> educationEntryList;

    public EducationWallet() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public List<EducationEntry> getEducationEntryList() {
        return educationEntryList;
    }

    public void setEducationEntryList(List<EducationEntry> educationEntryList) {
        this.educationEntryList = educationEntryList;
    }

    @Override
    public String toString() {
        return "EducationWallet{" +
                "id=" + id +
                ", departmentId=" + departmentId +
                ", coins=" + coins +
                ", companyName='" + companyName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", educationEntryList=" + educationEntryList +
                '}';
    }
}
