package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.helper.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    fieldVisibility = JsonAutoDetect.Visibility.ANY
)
public class AbsenceApiQuery implements Serializable {

    @JsonProperty("company_code")
    private String companyCode;

    @JsonProperty("worker_ext_ids")
    private List<String> workerExtIds;

    @JsonProperty("reasons")
    private Set<En_AbsenceReason> reasons;

    @JsonProperty("from")
    private Date from;

    @JsonProperty("to")
    private Date to;

    public AbsenceApiQuery() {
    }

    public Set<En_AbsenceReason> getReasons() {
        return reasons;
    }

    public void setReasons(Set<En_AbsenceReason> reasons) {
        this.reasons = reasons;
    }

    public boolean isValid() {
        return from != null && to != null && StringUtils.isNotEmpty(companyCode);
    }

    public List<String> getWorkerExtIds() {
        return workerExtIds;
    }

    public void setWorkerExtIds(List<String> workerExtIds) {
        this.workerExtIds = workerExtIds;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @Override
    public String toString() {
        return "AbsenceApiQuery{" +
                "workerExtIds=" + workerExtIds +
                ", from=" + from +
                ", to=" + to +
                ", companyCode='" + companyCode + '\'' +
                '}';
    }
}
