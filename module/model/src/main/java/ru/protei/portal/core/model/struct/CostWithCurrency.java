package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_Currency;

import java.io.Serializable;

@JsonAutoDetect
public class CostWithCurrency implements Serializable {

    @JsonProperty("ct")
    private Long cost;

    @JsonProperty("cr")
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
