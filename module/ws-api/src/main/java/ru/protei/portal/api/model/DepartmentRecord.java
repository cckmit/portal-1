package ru.protei.portal.api.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by turik on 17.08.16.
 */
@XmlRootElement(name = "department")
public class DepartmentRecord {

    @XmlElement(name = "company-code")
    private String companyCode;

    @XmlElement(name = "id")
    private long departmentId;

    @XmlElement(name = "name")
    private String departmentName;

    @XmlElement(name = "parent-id", nillable = true)
    private Long parentId;

    @XmlElement(name = "head-id", nillable = true)
    private Long headId;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getHeadId() {
        return headId;
    }

    public void setHeadId(Long headId) {
        this.headId = headId;
    }
}
