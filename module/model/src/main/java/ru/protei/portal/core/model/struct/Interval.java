package ru.protei.portal.core.model.struct;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Interval implements Serializable {
    public Date from;
    public Date to;

    public Interval() { }

    public Interval (Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() { return from; }

    public void setFrom(Date from) { this.from = from; }

    public Date getTo() { return to; }

    public void setTo(Date to) { this.to = to; }

    @Override
    public String toString() {
        return "Interval{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }

    public boolean isValid() {
        return this.from != null && this.to != null && (this.from.getTime() < this.to.getTime() || this.from.getTime() == this.to.getTime());
    }

    public boolean isEmpty() {
        return this.from == null && this.to == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interval interval = (Interval) o;
        return Objects.equals(from, interval.from) &&
                Objects.equals(to, interval.to);
    }
}