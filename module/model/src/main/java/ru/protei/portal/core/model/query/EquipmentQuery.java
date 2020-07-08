package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.*;

import java.util.Set;

/**
 * Фильтр по оборудованию
 */
public class EquipmentQuery extends BaseQuery {

    private Set<En_Organization> organizationCodes;

    private Set<En_EquipmentType> types;

    private String classifierCode;

    private String registerNumber;

    private Long managerId;

    private Long equipmentId;

    private Set<Long> projectIds;

    public Set<En_EquipmentType> getTypes() {
        return types;
    }

    public void setTypes( Set<En_EquipmentType> types ) {
        this.types = types;
    }

    public Set<En_Organization> getOrganizationCodes() {
        return organizationCodes;
    }

    public String getClassifierCode() {
        return classifierCode;
    }

    public void setClassifierCode( String classifierCode ) {
        this.classifierCode = classifierCode;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber( String registerNumber ) {
        this.registerNumber = registerNumber;
    }

    public void setOrganizationCodes( Set<En_Organization> organizationCodes ) {
        this.organizationCodes = organizationCodes;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId( Long managerId ) {
        this.managerId = managerId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public void setProjectIds(Set<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public Set<Long> getProjectIds() {
        return projectIds;
    }

    public EquipmentQuery() {
    }

    public EquipmentQuery( Set< En_EquipmentType > types ) {
        this.types = types;
    }

    public EquipmentQuery( String searchString, En_SortField sortField, En_SortDir sortDir, Set<En_Organization> organizationCodes,
                           Set< En_EquipmentType > types, String classifierCode, String registerNumber, Long managerId, Long equipmentId  ) {
        super(searchString, sortField, sortDir);
        this.organizationCodes = organizationCodes;
        this.types = types;
        this.classifierCode = classifierCode;
        this.registerNumber = registerNumber;
        this.managerId = managerId;
        this.equipmentId = equipmentId;
    }
}
