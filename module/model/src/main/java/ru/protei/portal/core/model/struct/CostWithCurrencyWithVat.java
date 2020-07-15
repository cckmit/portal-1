package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_Currency;

import java.io.Serializable;

public class CostWithCurrencyWithVat implements Serializable {

    private Long cost;
    private En_Currency currency;
    private Long vatPercent;

    public CostWithCurrencyWithVat() {
    }

    public CostWithCurrencyWithVat(Long cost, En_Currency currency, Long vatPercent) {
        this.cost = cost;
        this.currency = currency;
        this.vatPercent = vatPercent;
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

    public Long getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(Long vatPercent) {
        this.vatPercent = vatPercent;
    }
}
