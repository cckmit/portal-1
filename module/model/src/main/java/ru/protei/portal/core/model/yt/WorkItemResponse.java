package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonDeserialize(as = List.c )
public class WorkItemResponse {

    private List< WorkItem > items;

    @JsonValue
    public List<WorkItem> getItems() {
        return items;
    }

    public void setItems(List<WorkItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "WorkItemResponse{" +
                "items=" + items +
                '}';
    }
}
