package ru.protei.portal.ui.roomreservation.client.struct;

import java.io.Serializable;

public class YearMonthDay implements Serializable {

    private Integer year;
    private Integer month;
    private Integer dayOfMonth;

    public YearMonthDay() {}

    public YearMonthDay(Integer year, Integer month, Integer dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    @Override
    public String toString() {
        return "YearMonthDay{" +
                "year=" + year +
                ", month=" + month +
                ", dayOfMonth=" + dayOfMonth +
                '}';
    }
}
