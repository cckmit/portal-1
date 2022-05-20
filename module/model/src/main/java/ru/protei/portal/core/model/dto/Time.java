package ru.protei.portal.core.model.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Time implements Comparable<Time>, Serializable {

    private int hour; // 0-24
    private int minute; // 0-59

    public Time() {
    }

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Time(Time other) {
         if (other == null) return;
         this.hour = other.hour;
         this.minute = other.minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean after(Time other) {
        return compareTo(other) > 0;
    }

    public boolean before(Time other) {
        return compareTo(other) < 0;
    }

    @Override
    public int compareTo(Time o) {
        if (this.hour < o.hour)
            return -1;
        else if (this.hour > o.hour)
            return 1;
        if (this.minute < o.minute)
            return -1;
        else if (this.minute > o.minute)
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return hour == time.hour &&
                minute == time.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute);
    }

    @Override
    public String toString() {
        return "Time{" +
                "hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
