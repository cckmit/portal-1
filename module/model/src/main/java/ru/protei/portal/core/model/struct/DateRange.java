package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import ru.protei.portal.core.model.dict.En_DateIntervalType;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateRange implements Serializable {
    @JsonAlias ("type")
    private En_DateIntervalType intervalType;
    private Date from;
    private Date to;

    public DateRange() { }

    public DateRange(En_DateIntervalType intervalType, Date from, Date to) {
        if(intervalType == null) {
            this.intervalType = En_DateIntervalType.FIXED;
        } else {
            this.intervalType = intervalType;
        }
        this.from = from;
        this.to = to;
    }

    public En_DateIntervalType getIntervalType() { return intervalType; }

    public void setIntervalType(En_DateIntervalType intervalType) {
        this.intervalType = intervalType;
    }

    public Date getFrom() { return from; }

    public void setFrom(Date from) { this.from = from; }

    public Date getTo() { return to; }

    public void setTo(Date to) { this.to = to; }
}