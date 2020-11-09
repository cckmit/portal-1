package ru.protei.portal.core.model.dto;

import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.core.model.dto.Project.*;

@JdbcEntity(table = "project")
public class ProjectTechnicalSupportValidityReportInfo {
    /**
     * Идентификатор записи о проекте
     */
    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    /**
     * Название проекта
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = "CASE_NAME",
            table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS)
    private String name;

    /**
     * Срок действия технической поддержки
     */
    @JdbcColumn(name = "technical_support_validity")
    private Date technicalSupportValidity;

    /**
     * Заказчик
     */
    @JdbcJoinedColumn(mappedColumn = "cname", joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object", sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = "initiator_company", remoteColumn = "id", table = "company")})
    private String customerName;

    /**
     * Руководитель
     */
    @JdbcJoinedColumn(table = "case_member", sqlTableAlias = CASE_MEMBER_ALIAS, mappedColumn = "MEMBER_ID", joinData = {
            @JdbcJoinData(localColumn = "id", remoteColumn = "CASE_ID"),
            @JdbcJoinData(remoteColumn = "MEMBER_ROLE_ID", value = "1")
    })
    private Long headManagerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTechnicalSupportValidity() {
        return technicalSupportValidity;
    }

    public void setTechnicalSupportValidity(Date technicalSupportValidity) {
        this.technicalSupportValidity = technicalSupportValidity;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getHeadManagerId() {
        return headManagerId;
    }

    public void setHeadManagerId(Long headManager) {
        this.headManagerId = headManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectTechnicalSupportValidityReportInfo)) return false;
        ProjectTechnicalSupportValidityReportInfo that = (ProjectTechnicalSupportValidityReportInfo) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(technicalSupportValidity, that.technicalSupportValidity) &&
                Objects.equals(customerName, that.customerName) &&
                Objects.equals(headManagerId, that.headManagerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, technicalSupportValidity, customerName, headManagerId);
    }

    @Override
    public String toString() {
        return "ProjectTechnicalSupportValidityReportInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", customerName=" + customerName +
                ", technicalSupportValidity=" + technicalSupportValidity +
                ", headManagerId=" + headManagerId +
                '}';
    }
}
