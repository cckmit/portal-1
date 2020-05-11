package ru.protei.portal.ui.roomreservation.client.struct;

import java.io.Serializable;

public class Day implements Serializable {

    private Integer dayOfMonth;
    private Integer dayOfWeek;

    public Day() {}

    public Day(Integer dayOfMonth, Integer dayOfWeek) {
        this.dayOfMonth = dayOfMonth;
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        return "Day{" +
                "dayOfMonth=" + dayOfMonth +
                ", dayOfWeek=" + dayOfWeek +
                '}';
    }
}
