package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Параметры фильтра обращений
 */
@JsonAutoDetect
public class IssueFilterParams implements Serializable {

    @JsonProperty("params")
    private List<IssueFilterParam> paramsList;

    public IssueFilterParams () {
        paramsList = new ArrayList<IssueFilterParam>();
    }

    @JsonIgnore
    public List<IssueFilterParam> getItems() {
        return paramsList;
    }
}
