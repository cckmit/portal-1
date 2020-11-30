package ru.protei.portal.core.model.dict;


import ru.protei.winter.core.utils.enums.HasId;

public enum En_ContractDatesType implements HasId {
    PREPAYMENT(2),
    POSTPAYMENT(0),
    SUPPLY(1),
    ;

    En_ContractDatesType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private int id;
}
