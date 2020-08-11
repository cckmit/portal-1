package ru.protei.portal.core.model.struct;

import java.io.Serializable;

public class EmployeeBirthday implements Serializable {

    private Long id;
    private String name;
    /**
     * 1-based month (1-12)
     */
    private Integer birthdayMonth;
    /**
     * 1-based day (1-31)
     */
    private Integer birthdayDayOfMonth;

    public EmployeeBirthday() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthdayMonth() {
        return birthdayMonth;
    }

    public void setBirthdayMonth(Integer birthdayMonth) {
        this.birthdayMonth = birthdayMonth;
    }

    public Integer getBirthdayDayOfMonth() {
        return birthdayDayOfMonth;
    }

    public void setBirthdayDayOfMonth(Integer birthdayDayOfMonth) {
        this.birthdayDayOfMonth = birthdayDayOfMonth;
    }

    @Override
    public String toString() {
        return "EmployeeBirthday{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthdayMonth=" + birthdayMonth +
                ", birthdayDayOfMonth=" + birthdayDayOfMonth +
                '}';
    }
}
