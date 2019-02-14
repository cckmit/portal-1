package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ContractDatesType;

import java.io.Serializable;
import java.util.Date;


public class ContractDate implements Serializable {

    @JsonProperty("d")
    private Date date;

    @JsonProperty("c")
    private String comment;

    @JsonProperty("t")
    private En_ContractDatesType type;

    public ContractDate() {}

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
}
