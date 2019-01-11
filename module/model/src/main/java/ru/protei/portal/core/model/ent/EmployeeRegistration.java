package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Анкета нового сотрудника
 */
@JdbcEntity(table = "employee_registration")
public class EmployeeRegistration extends AuditableObject implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
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
    private String position;

    /**
     * Расположение рабочего места
     */
    @JdbcColumn
    private String workplace;

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
     * Доступ к офисной телефонии
     */
    @JdbcEnumerated(EnumType.ORDINAL)
    @JdbcColumnCollection(name = "phone_office_type_list", separator = ",")
    private Set<En_PhoneOfficeType> phoneOfficeTypeList;

    /**
     * Создатель анкеты
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = "CREATOR", table = "case_object", sqlTableAlias = "CO")
    private Long creatorId;

    /**
     * Руководитель
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = "INITIATOR", table = "case_object", sqlTableAlias = "CO")
    private Long headOfDepartmentId;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = "id", remoteColumn = "id", table = "case_object"),
            @JdbcJoinPath(localColumn = "INITIATOR", remoteColumn = "id", table = "Person")
    }, mappedColumn = "displayShortName")
    private String headOfDepartmentShortName;

    /**
     * Комментарий
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "INFO")
    private String comment;

    /**
     * ФИО сотрудника
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "CASE_NAME", sqlTableAlias = "CO")
    private String employeeFullName;

    /**
     * Дата создания
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "CREATED", sqlTableAlias = "CO")
    private Date created;

    /**
     * Состояние
     */
    @JdbcEnumerated(EnumType.ID)
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "STATE", sqlTableAlias = "CO")
    private En_CaseState state;

    @JdbcOneToMany(localColumn = "id", table = "case_link", remoteColumn = "case_id", additionalConditions = {
            @JdbcManyJoinData(remoteColumn = "link_type", value = "YT", valueClass = String.class)
    })
    private Set<CaseLink> youtrackIssues;

    /**
     *  испытательный срок
     */
    @JdbcColumn(name ="probation_period")
    private Integer probationPeriodMonth;

    /**
     * комментраий к списку ресорсов
     */
    @JdbcColumn(name ="resource_comment")
    private String resourceComment;

    /**
     * Операционная система
     */
    @JdbcColumn(name ="operating_system")
    private String operatingSystem;

    /**
     * Дополнительное ПО
     */
    @JdbcColumn(name ="additional_soft")
    private String additionalSoft;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
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

    public void setPhoneOfficeTypeList( Set<En_PhoneOfficeType> phoneOfficeTypeList ) {
        this.phoneOfficeTypeList = phoneOfficeTypeList;
    }

    public Set<En_PhoneOfficeType> getPhoneOfficeTypeList() {
        return phoneOfficeTypeList;
    }

    public PersonShortView getHeadOfDepartment() {
        if (headOfDepartmentId == null)
            return null;
        return new PersonShortView(headOfDepartmentShortName, headOfDepartmentId);
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public En_CaseState getState() {
        return state;
    }

    public void setState(En_CaseState state) {
        this.state = state;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getHeadOfDepartmentId() {
        return headOfDepartmentId;
    }

    public void setHeadOfDepartmentId(Long headOfDepartmentId) {
        this.headOfDepartmentId = headOfDepartmentId;
    }

    public String getHeadOfDepartmentShortName() {
        return headOfDepartmentShortName;
    }

    public void setHeadOfDepartmentShortName(String headOfDepartmentShortName) {
        this.headOfDepartmentShortName = headOfDepartmentShortName;
    }

    public Set<CaseLink> getYoutrackIssues() {
        return youtrackIssues;
    }

    public void setYoutrackIssues(Set<CaseLink> youtrackIssues) {
        this.youtrackIssues = youtrackIssues;
    }

    public Integer getProbationPeriodMonth() {
        return probationPeriodMonth;
    }

    public void setProbationPeriodMonth( Integer probationPeriodMonth ) {
        this.probationPeriodMonth = probationPeriodMonth;
    }

    public String getResourceComment() {
        return resourceComment;
    }

    public void setResourceComment( String resourceComment ) {
        this.resourceComment = resourceComment;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem( String operatingSystem ) {
        this.operatingSystem = operatingSystem;
    }

    public String getAdditionalSoft() {
        return additionalSoft;
    }

    public void setAdditionalSoft( String additionalSoft ) {
        this.additionalSoft = additionalSoft;
    }

    @Override
    public String getAuditType() {
        return "EmployeeRegistration";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmployeeRegistration that = (EmployeeRegistration) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "EmployeeRegistration{" +
                "id=" + id +
                ", employmentDate=" + employmentDate +
                ", employmentType=" + employmentType +
                ", withRegistration=" + withRegistration +
                ", position='" + position + '\'' +
                ", workplace='" + workplace + '\'' +
                ", equipmentList=" + equipmentList +
                ", resourceList=" + resourceList +
                ", phoneOfficeTypeList=" + phoneOfficeTypeList +
                ", creatorId=" + creatorId +
                ", headOfDepartmentId=" + headOfDepartmentId +
                ", headOfDepartmentShortName='" + headOfDepartmentShortName + '\'' +
                ", comment='" + comment + '\'' +
                ", employeeFullName='" + employeeFullName + '\'' +
                ", created=" + created +
                ", state=" + state +
                ", probationPeriodMonth=" + probationPeriodMonth +
                ", resourceComment='" + resourceComment + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", additionalSoft='" + additionalSoft + '\'' +
                ", youtrackIssues=" + youtrackIssues +
                '}';
    }
}
