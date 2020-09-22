package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_Currency;

import java.io.Serializable;

public class MoneyWithCurrencyWithVat implements Serializable {

    private Money money;
    private En_Currency currency;
    private Long vatPercent;

    public MoneyWithCurrencyWithVat() {
    }

    public MoneyWithCurrencyWithVat(Money money, En_Currency currency, Long vatPercent) {
        this.money = money;
        this.currency = currency;
        this.vatPercent = vatPercent;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
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
