package ru.protei.portal.core.model.enterprise1c.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkPersonInfo1C {
    @JsonProperty("PersonDisplayName")
    private String personDisplayName;

    @JsonProperty("WorkedDays")
    private Integer workedDays;

    @JsonProperty("WorkedHours")
    private Integer workedHours;

    @JsonProperty("SickDays")
    private Integer sickDays;

    @JsonProperty("TripDays")
    private Integer tripDays;

    @JsonProperty("VacationDays")
    private Integer vacationDays;

    @JsonProperty("OtherNotWorked")
    private Integer otherNotWorked;

    public String getPersonDisplayName() {
        return personDisplayName;
    }

    public void setPersonDisplayName(String personDisplayName) {
        this.personDisplayName = personDisplayName;
    }

    public Integer getWorkedDays() {
        return workedDays;
    }

    public void setWorkedDays(Integer workedDays) {
        this.workedDays = workedDays;
    }

    public Integer getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(Integer workedHours) {
        this.workedHours = workedHours;
    }

    public Integer getSickDays() {
        return sickDays;
    }

    public void setSickDays(Integer sickDays) {
        this.sickDays = sickDays;
    }

    public Integer getTripDays() {
        return tripDays;
    }

    public void setTripDays(Integer tripDays) {
        this.tripDays = tripDays;
    }

    public Integer getVacationDays() {
        return vacationDays;
    }

    public void setVacationDays(Integer vacationDays) {
        this.vacationDays = vacationDays;
    }

    public Integer getOtherNotWorked() {
        return otherNotWorked;
    }

    public void setOtherNotWorked(Integer otherNotWorked) {
        this.otherNotWorked = otherNotWorked;
    }

    @Override
    public String toString() {
        return "WorkPersonInfo1C{" +
                "personDisplayName='" + personDisplayName + '\'' +
                ", workedDays=" + workedDays +
                ", workedHours=" + workedHours +
                ", sickDays=" + sickDays +
                ", tripDays=" + tripDays +
                ", vacationDays=" + vacationDays +
                ", otherNotWorked=" + otherNotWorked +
                '}';
    }
}
