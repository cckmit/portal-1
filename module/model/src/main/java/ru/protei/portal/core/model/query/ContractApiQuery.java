package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ContractApiQuery implements Serializable {

    @JsonProperty("refKeys")
    private List<String> refKeys;

    @JsonProperty("openStateDate")
    private Date openStateDate;

    public ContractApiQuery() {
    }

    public List<String> getRefKeys() {
        return refKeys;
    }

    public void setRefKeys(List<String> refKeys) {
        this.refKeys = refKeys;
    }

    public Date getOpenStateDate() {
        return openStateDate;
    }

    public void setOpenStateDate(Date openStateDate) {
        this.openStateDate = openStateDate;
    }

    @Override
    public String toString() {
        return "ContractApiQuery{" +
                "refKeys=" + refKeys +
                ", openStateDate=" + openStateDate +
                '}';
    }
}
