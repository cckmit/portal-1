package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DecimalNumber;

import java.util.List;
import java.util.Set;

/**
 * Фильтр по оборудованию
 */
public class EquipmentQuery extends BaseQuery {

    private String name;

    private List<DecimalNumber> decimalNumbers;

    private Set<En_EquipmentStage> stages;

    private Set<En_EquipmentType> types;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<DecimalNumber> getDecimalNumbers() {
        return decimalNumbers;
    }

    public void setDecimalNumbers( List<DecimalNumber> decimalNumbers ) {
        this.decimalNumbers = decimalNumbers;
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

    public EquipmentQuery( String name, List<DecimalNumber> decimalNumbers, Set<En_EquipmentStage> stages, Set<En_EquipmentType> types ) {
        this.name = name;
        this.decimalNumbers = decimalNumbers;
        this.stages = stages;
        this.types = types;
    }
}
