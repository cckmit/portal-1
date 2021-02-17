package ru.protei.portal.core.model.dict;


import ru.protei.winter.core.utils.enums.HasId;

public enum ContractCostType implements HasId {
    EQUIPMENT(1),
    SOFTWARE(2),
    SERVICES(3);

    ContractCostType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private int id;
}
