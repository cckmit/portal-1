package ru.protei.portal.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Отрезок времени (00:00-23:59)
 * время старта и окончания входят в описываемый отрезок
 */
public class TimeInterval implements Comparable<TimeInterval>, Serializable {

    private Time from;
    private Time to;

    public TimeInterval() {
    }

    public TimeInterval(Time from, Time to) {
        this.from = from;
        this.to = to;
    }

    public TimeInterval(Date from, Date to) {
        if (from == null || to == null) return;
        this.from = new Time(from.getHours(), from.getMinutes());
        this.to = new Time(to.getHours(), to.getMinutes());
    }


    public Time getTo() {
        return to;
    }

    public Time getFrom() {
        return from;
    }

    @JsonIgnore
    public boolean isValid() {
        return from != null && to != null && from.before(to);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return from == null && to == null;
    }

    public boolean before(TimeInterval other) throws IllegalArgumentException {
        checkComparison(other);
        return to.before(other.from);
    }

    public boolean after(TimeInterval other) throws IllegalArgumentException {
        checkComparison(other);
        return from.after(other.to);
    }

    public boolean overlaps(TimeInterval other) throws IllegalArgumentException {
        checkComparison(other);
        return !before(other) && !after(other);
    }

    public boolean includes(TimeInterval other) throws IllegalArgumentException {
        checkComparison(other);
        return from.compareTo(other.from) <= 0 && to.compareTo(other.to) >= 0;
    }

    public boolean merge(TimeInterval other) {
        if (!overlaps(other))
            return false;
        from = from.before(other.from) ? new Time(from) : new Time(other.from);
        to = to.after(other.to) ? new Time(to) : new Time(other.to);
        return true;
    }

    private void checkComparison(TimeInterval other) throws IllegalArgumentException {
        if (!isValid() || !other.isValid()) {
            throw new IllegalArgumentException("Comparing uninitialized time periods");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeInterval period = (TimeInterval) o;
        return Objects.equals(from, period.from) && Objects.equals(to, period.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public int compareTo(TimeInterval o) {
        int startCompare = compare(this.from, o.from);

        return startCompare == 0 ? compare(this.to, o.to) : startCompare;
    }

    public int compare(Time time1, Time time2) {
        if (time1 == time2) {
            return 0;
        } else if (time1 == null) {
            return -1;
        } else if (time2 == null) {
            return 1;
        } else {
            return time1.compareTo(time2);
        }
    }

    @Override
    public String toString() {
        return "TimePeriod{" +
                "end=" + to +
                ", start=" + from +
                '}';
    }
}
