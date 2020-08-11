package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_Gender;

import java.io.Serializable;

public class EmployeeBirthday implements Serializable {

    private Long id;
    private String name;
    private En_Gender gender;
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

    public En_Gender getGender() {
        return gender;
    }

    public void setGender(En_Gender gender) {
        this.gender = gender;
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
                ", gender=" + gender +
                ", birthdayMonth=" + birthdayMonth +
                ", birthdayDayOfMonth=" + birthdayDayOfMonth +
                '}';
    }
}
