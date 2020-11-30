package ru.protei.portal.core.model.struct;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class EmployeesBirthdays implements Serializable {

    private Date dateFrom;
    private Date dateUntil;
    private List<EmployeeBirthday> birthdays;

    public EmployeesBirthdays() {
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateUntil() {
        return dateUntil;
    }

    public void setDateUntil(Date dateUntil) {
        this.dateUntil = dateUntil;
    }

    public List<EmployeeBirthday> getBirthdays() {
        return birthdays;
    }

    public void setBirthdays(List<EmployeeBirthday> birthdays) {
        this.birthdays = birthdays;
    }

    @Override
    public String toString() {
        return "EmployeesBirthdays{" +
                "dateFrom=" + dateFrom +
                ", dateUntil=" + dateUntil +
                ", birthdays=" + birthdays +
                '}';
    }
}
