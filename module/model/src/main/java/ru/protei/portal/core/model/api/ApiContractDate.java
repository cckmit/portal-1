package ru.protei.portal.core.model.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.dict.En_Currency;

import java.io.Serializable;
import java.util.Date;

@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY
)
public class ApiContractDate implements Serializable {

    @JsonProperty("date")
    private Date date;

    @JsonProperty("cost")
    private Long cost;

    @JsonProperty("cost_currency")
    private En_Currency currency;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("type")
    private En_ContractDatesType type;

    public ApiContractDate() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public En_ContractDatesType getType() {
        return type;
    }

    public void setType(En_ContractDatesType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ApiContractDate{" +
                "date=" + date +
                ", cost=" + cost +
                ", currency=" + currency +
                ", comment='" + comment + '\'' +
                ", type=" + type +
                '}';
    }
}
