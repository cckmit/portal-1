package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_Currency;

import java.io.Serializable;

public class MoneyWithCurrency implements Serializable {

    private Money money;
    private En_Currency currency;

    public MoneyWithCurrency() {}

    public MoneyWithCurrency(Money money, En_Currency currency) {
        this.money = money;
        this.currency = currency;
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
}
