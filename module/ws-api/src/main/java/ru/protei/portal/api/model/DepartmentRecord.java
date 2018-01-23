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

    private String departmentId;

    private String departmentName;

    private String parentId;

    private String headId;

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
    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
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
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @XmlElement(name = "head-id")
    public String getHeadId() {
        return headId;
    }

    public void setHeadId(String headId) {
        this.headId = headId;
    }

    public void copy(CompanyDepartment d) {
        setCompanyCode(d.getCompanyExternalCode());

        setDepartmentId(d.getExternalId());
        setDepartmentName(d.getName());
        setParentId(d.getParentExternalId());
        setHeadId(d.getHeadExternalId());
    }

    @Override
    public String toString() {
        return "DepartmentRecord{" +
                "companyCode='" + companyCode + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", parentId='" + parentId + '\'' +
                ", headId='" + headId + '\'' +
                '}';
    }
}
