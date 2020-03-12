package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "education_wallet")
public class EducationWallet implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="dep_id")
    private String departmentId;

    @JdbcColumn(name="coins")
    private Integer coins;

    @JdbcJoinedColumn(localColumn = "dep_id", table = "company_dep", remoteColumn = "id", mappedColumn = "dep_name")
    private String departmentName;

    public EducationWallet() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    @Override
    public String toString() {
        return "EducationWallet{" +
                "id=" + id +
                ", departmentId='" + departmentId + '\'' +
                ", coins=" + coins +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
