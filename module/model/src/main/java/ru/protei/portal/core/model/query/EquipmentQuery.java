package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.DecimalNumber;

import java.util.List;
import java.util.Set;

/**
 * Фильтр по оборудованию
 */
public class EquipmentQuery extends BaseQuery {

    private String name;

    private Set<En_OrganizationCode> organizationCodes;

    private Set<En_EquipmentStage> stages;

    private Set<En_EquipmentType> types;

    private String classifierCode;

    private String registerNumber;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Set<En_EquipmentStage> getStages() {
        return stages;
    }

    public void setStages( Set<En_EquipmentStage> stages ) {
        this.stages = stages;
    }

    public Set<En_EquipmentType> getTypes() {
        return types;
    }

    public void setTypes( Set<En_EquipmentType> types ) {
        this.types = types;
    }

    public Set<En_OrganizationCode> getOrganizationCodes() {
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

    public void setOrganizationCodes( Set<En_OrganizationCode> organizationCodes ) {
        this.organizationCodes = organizationCodes;
    }

    public EquipmentQuery() {
    }

    public EquipmentQuery( Set< En_EquipmentType > types ) {
        this.types = types;
    }

    public EquipmentQuery( String name, Set< En_OrganizationCode > organizationCodes, Set< En_EquipmentStage > stages, Set< En_EquipmentType > types, String classifierCode, String registerNumber ) {
        this.name = name;
        this.organizationCodes = organizationCodes;
        this.stages = stages;
        this.types = types;
        this.classifierCode = classifierCode;
        this.registerNumber = registerNumber;
    }
}
