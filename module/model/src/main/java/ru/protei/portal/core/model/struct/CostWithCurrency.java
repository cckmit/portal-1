package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_Currency;

import java.io.Serializable;

public class CostWithCurrency implements Serializable {

    private Long cost;
    private En_Currency currency;

    public CostWithCurrency() {}

    public CostWithCurrency(Long cost, En_Currency currency) {
        this.cost = cost;
        this.currency = currency;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public En_Currency getCurrency() {
        return currency;
    }

    public void setCurrency(En_Currency currency) {
        this.currency = currency;
    }
}
