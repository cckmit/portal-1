package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_EmployeeEquipment;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.core.model.dict.En_InternalResource;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Анкета нового сотрудника
 */
public class Questionnaire implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    /**
     * Дата приёма
     */
    @JdbcColumn(name = "employment_date")
    private Date employmentDate;

    /**
     * Занятость
     */
    @JdbcColumn(name = "employment_type")
    @JdbcEnumerated(EnumType.ORDINAL)
    private En_EmploymentType employmentType;

    /**
     * С оформлением или без
     */
    @JdbcColumn(name = "with_registration")
    private boolean withRegistration;

    /**
     * Должность
     */
    @JdbcColumn
    private String post;

    /**
     * Расположение рабочего места
     */
    @JdbcColumn(name = "workplace_info")
    private String workplaceInfo;

    /**
     * Оборудование для рабочего места нового сотрудника
     */
    @JdbcEnumerated(EnumType.ORDINAL)
    @JdbcColumnCollection(name = "equipment_list", separator = ",")
    private Set<En_EmployeeEquipment> equipmentList;

    /**
     * Доступ к внутренним ресурсам
     */
    @JdbcEnumerated(EnumType.ORDINAL)
    @JdbcColumnCollection(name = "resource_list", separator = ",")
    private Set<En_InternalResource> resourceList;

    /**
     * Создатель анкеты
     */
    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "CREATOR", remoteColumn = "id", table = "Person")
    })
    private Person creator;


    /**
     * Руководитель
     */
    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "INITIATOR", remoteColumn = "id", sqlTableAlias = "PersonInitiator", table = "Person")
    })
    private Person headOfDepartment;

    /**
     * Комментарий
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "INFO")
    private String comment;

    /**
     * ФИО сотрудника
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "CASE_NAME")
    private String employeeFullName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
    }

    public En_EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(En_EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public boolean isWithRegistration() {
        return withRegistration;
    }

    public void setWithRegistration(boolean withRegistration) {
        this.withRegistration = withRegistration;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getWorkplaceInfo() {
        return workplaceInfo;
    }

    public void setWorkplaceInfo(String workplaceInfo) {
        this.workplaceInfo = workplaceInfo;
    }

    public Set<En_EmployeeEquipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(Set<En_EmployeeEquipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public Set<En_InternalResource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(Set<En_InternalResource> resourceList) {
        this.resourceList = resourceList;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public Person getHeadOfDepartment() {
        return headOfDepartment;
    }

    public void setHeadOfDepartment(Person headOfDepartment) {
        this.headOfDepartment = headOfDepartment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public void setEmployeeFullName(String employeeFullName) {
        this.employeeFullName = employeeFullName;
    }

    @Override
    public String toString() {
        return "Questionnaire{" +
                "id=" + id +
                ", employmentDate=" + employmentDate +
                ", employmentType=" + employmentType +
                ", withRegistration=" + withRegistration +
                ", post='" + post + '\'' +
                ", workplaceInfo='" + workplaceInfo + '\'' +
                ", equipmentList=" + equipmentList +
                ", resourceList=" + resourceList +
                ", creator=" + creator +
                ", headOfDepartment=" + headOfDepartment +
                ", comment='" + comment + '\'' +
                ", employeeFullName='" + employeeFullName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Questionnaire that = (Questionnaire) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
