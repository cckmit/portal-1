package ru.protei.portal.core.model.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.io.Serializable;
import java.util.Date;

@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY
)
public class ApiAbsence implements Serializable {

    @JsonProperty("person_id")
    private Long personId;

    @JsonProperty("worker_ext_id")
    private String workerExtId;

    @JsonProperty("reason")
    private En_AbsenceReason reason;

    @JsonProperty("from")
    private Date fromTime;

    @JsonProperty("to")
    private Date tillTime;

    public ApiAbsence() {
    }

    public ApiAbsence(PersonAbsence absence) {
        this.personId = absence.getPersonId();
        this.reason = absence.getReason();
        this.fromTime = absence.getFromTime();
        this.tillTime = absence.getTillTime();
    }

    public ApiAbsence withWorkerId(String workerId) {
        this.workerExtId = workerId;
        return this;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getWorkerExtId() {
        return workerExtId;
    }

    public void setWorkerExtId(String workerExtId) {
        this.workerExtId = workerExtId;
    }

    public En_AbsenceReason getReason() {
        return reason;
    }

    public void setReason(En_AbsenceReason reason) {
        this.reason = reason;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getTillTime() {
        return tillTime;
    }

    public void setTillTime(Date tillTime) {
        this.tillTime = tillTime;
    }

    @Override
    public String toString() {
        return "ApiAbsence{" +
                "personId=" + personId +
                ", workerExtId='" + workerExtId + '\'' +
                ", reason=" + reason +
                ", fromTime=" + fromTime +
                ", tillTime=" + tillTime +
                '}';
    }
}
