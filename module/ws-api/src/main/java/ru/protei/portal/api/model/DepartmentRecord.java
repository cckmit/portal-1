package ru.protei.portal.api.model;

import ru.protei.portal.core.model.ent.CompanyDepartment;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by turik on 17.08.16.
 */
@XmlRootElement(name = "department")
public class DepartmentRecord {

    private String companyCode;

    private long departmentId;

    private String departmentName;

    private Long parentId;

    private Long headId;

    public DepartmentRecord() {}

    public DepartmentRecord(CompanyDepartment d) {
        copy(d);
    }

    @XmlElement(name = "company-code", required = true)
    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @XmlElement(name = "id", required = true)
    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    @XmlElement(name = "name", required = true)
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @XmlElement(name = "parent-id")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @XmlElement(name = "head-id")
    public Long getHeadId() {
        return headId;
    }

    public void setHeadId(Long headId) {
        this.headId = headId;
    }

    public void copy(CompanyDepartment d) {
        setCompanyCode(d.getExternalCode());

        setDepartmentId(d.getExternalId());
        setDepartmentName(d.getName());
        setParentId(d.getParentExternalId());
        setHeadId(d.getHeadId());
    }
}
