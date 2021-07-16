package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.struct.Interval;

import java.util.ArrayList;
import java.util.List;

public class ScheduleItem {

    private List<Integer> daysOfWeek;
    private List<Interval> times;

    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    public ScheduleItem() {}

    public ScheduleItem(List<Integer> daysOfWeek, List<Interval> times) {
        this.daysOfWeek = daysOfWeek;
        this.times = times;
    }

    public void setDaysOfWeek(List<Integer> daysOfWeek) {
        if (this.daysOfWeek == null) daysOfWeek = new ArrayList<>();
        this.daysOfWeek = daysOfWeek;
    }

    public List<Interval> getTimes() {
        return times;
    }

    public void setTimes(List<Interval> times) {
        if (this.times == null) times = new ArrayList<>();
        this.times = times;
    }
}
