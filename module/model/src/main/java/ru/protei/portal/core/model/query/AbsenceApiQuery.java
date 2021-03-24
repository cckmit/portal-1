package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.helper.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    fieldVisibility = JsonAutoDetect.Visibility.ANY
)
public class AbsenceApiQuery implements Serializable {

    @JsonProperty("worker_ext_ids")
    private List<String> workerExtIds;

    @JsonProperty("from")
    private Date from;

    @JsonProperty("to")
    private Date to;

    @JsonProperty("company_code")
    private String companyCode;

    public AbsenceApiQuery() {
    }

    public AbsenceApiQuery(List<String> workerExtIds, Date from, Date to, String companyCode) {
        this.workerExtIds = workerExtIds;
        this.from = from;
        this.to = to;
        this.companyCode = companyCode;
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
