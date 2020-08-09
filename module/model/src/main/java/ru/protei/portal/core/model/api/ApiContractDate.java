package ru.protei.portal.core.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ContractDatesType;

import java.io.Serializable;
import java.util.Date;

public class ApiContractDate implements Serializable {

    @JsonProperty("date")
    private Date date;

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
                ", comment='" + comment + '\'' +
                ", type=" + type +
                '}';
    }
}
