package ru.protei.portal.core.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.helper.StringUtils;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY
)
public class PersonAbsenceApiInfo implements Serializable {
    @JsonProperty("person_id")
    private Long personId;

    @JsonProperty("company_code")
    private String companyCode;

    @JsonProperty("worker_ext_id")
    private String workerId;

    @JsonProperty("reason")
    private En_AbsenceReason reason;

    @JsonProperty("from")
    private Date fromTime;

    @JsonProperty("till")
    private Date tillTime;

    @JsonProperty("user_comment")
    private String userComment;

    public PersonAbsenceApiInfo() {
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
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

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public boolean isValid() {
        return reason != null &&
                fromTime != null && tillTime != null &&
                ((StringUtils.isNotEmpty(companyCode) && workerId != null) ^ personId != null) ;
    }

    @Override
    public String toString() {
        return "PersonAbsenceApiInfo{" +
                ", personId=" + personId +
                ", workerId=" + workerId +
                ", reason=" + reason +
                ", fromTime=" + fromTime +
                ", tillTime=" + tillTime +
                ", userComment='" + userComment + '\'' +
                '}';
    }
}
