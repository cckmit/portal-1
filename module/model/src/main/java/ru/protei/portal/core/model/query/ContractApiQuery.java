package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import ru.protei.portal.core.model.ent.CaseState;

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

    private List<String> refKeys;

    private Date openStateDate;

    private List<CaseState> states;

    private List<Long> organizationIds;

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

    public List<CaseState> getStates() {
        return states;
    }

    public void setStates(List<CaseState> states) {
        this.states = states;
    }

    public List<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    @Override
    public String toString() {
        return "ContractApiQuery{" +
                "refKeys=" + refKeys +
                ", openStateDate=" + openStateDate +
                ", states=" + states +
                ", organizationIds=" + organizationIds +
                '}';
    }
}
