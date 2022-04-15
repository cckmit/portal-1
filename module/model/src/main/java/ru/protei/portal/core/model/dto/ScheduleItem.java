package ru.protei.portal.core.model.dto;


import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ScheduleItem implements Serializable {

    private List<TimeInterval> times;
    private List<Integer> daysOfWeek;

    public ScheduleItem() {}

    public ScheduleItem(List<Integer> daysOfWeek, List<TimeInterval> times) {
        this.daysOfWeek = daysOfWeek;
        this.times = times;
    }
    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<TimeInterval> getTimes() {
        return times;
    }

    public void setTimes(List<TimeInterval> times) {
        this.times = times;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleItem)) return false;
        ScheduleItem that = (ScheduleItem) o;
        return Objects.equals(times, that.times) && Objects.equals(daysOfWeek, that.daysOfWeek);
    }

    @Override
    public int hashCode() {
        return Objects.hash(times, daysOfWeek);
    }
}
