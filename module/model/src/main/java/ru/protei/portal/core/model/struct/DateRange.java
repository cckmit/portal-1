package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import ru.protei.portal.core.model.dict.En_DateIntervalType;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateRange implements Serializable {
    @JsonAlias ({"type", "intervalType"})
    private En_DateIntervalType intervalType;
    private Date from;
    private Date to;

    public DateRange() { }

    public DateRange(En_DateIntervalType intervalType, Date from, Date to) {
        this.intervalType = intervalType;
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

    @Override
    public String toString() {
        return "DateRange{" +
                "intervalType=" + intervalType +
                ", from=" + from +
                ", to=" + to +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange dateRange = (DateRange) o;
        return Objects.equals(intervalType, dateRange.intervalType) &&
                Objects.equals(from, dateRange.from) &&
                Objects.equals(to, dateRange.to);
    }
}