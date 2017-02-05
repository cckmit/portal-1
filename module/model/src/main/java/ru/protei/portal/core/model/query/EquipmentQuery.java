package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Set;

/**
 * Фильтр по оборудованию
 */
public class EquipmentQuery extends BaseQuery {

    private String name;

    private String classifierCode;

    private String pdraRegNum;

    private String pamrRegNum;

    private Set<En_EquipmentStage> stages;

    private Set<En_EquipmentType> types;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getClassifierCode() {
        return classifierCode;
    }

    public void setClassifierCode( String classifierCode ) {
        this.classifierCode = classifierCode;
    }

    public String getPDRA_RegisterNumber() {
        return pdraRegNum;
    }

    public void setPdraRegNum( String pdraRegNum ) {
        this.pdraRegNum = pdraRegNum;
    }

    public String getPAMR_RegisterNumber() {
        return pamrRegNum;
    }

    public void setPamrRegNum( String pamrRegNum ) {
        this.pamrRegNum = pamrRegNum;
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

    public EquipmentQuery() {
    }

    public EquipmentQuery( String name, String classifierCode, String pdraRegNum, String pamrRegNum, Set<En_EquipmentStage> stages, Set<En_EquipmentType> types ) {
        this.name = name;
        this.classifierCode = classifierCode;
        this.pdraRegNum = pdraRegNum;
        this.pamrRegNum = pamrRegNum;
        this.stages = stages;
        this.types = types;
    }
}
