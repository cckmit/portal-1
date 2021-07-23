package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.struct.Interval;

import java.io.Serializable;
import java.util.List;

public class ScheduleItem implements Serializable {

    private List<Interval> times;
    private List<Integer> daysOfWeek;

    public ScheduleItem() {}

    public ScheduleItem(List<Integer> daysOfWeek, List<Interval> times) {
        this.daysOfWeek = daysOfWeek;
        this.times = times;
    }
    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<Interval> getTimes() {
        return times;
    }

    public void setTimes(List<Interval> times) {
        this.times = times;
    }
}
