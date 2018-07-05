package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationQuery extends BaseQuery {

    private Long applicationId;
    private List<Long> serverIds;
    private String comment;

    public ApplicationQuery() {
        this("", En_SortField.id, En_SortDir.ASC);
    }

    public ApplicationQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
    }

    public static ApplicationQuery forId(Long applicationId) {
        ApplicationQuery query = new ApplicationQuery();
        query.setApplicationId(applicationId);
        return query;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public List<Long> getServerIds() {
        return serverIds;
    }

    public void setServerIds(List<Long> serverIds) {
        this.serverIds = serverIds;
    }

    public void setServerId(Long serverId) {
        this.serverIds = Collections.singletonList(serverId);
    }

    public void addServerId(Long serverId) {
        if (this.serverIds == null) {
            this.serverIds = new ArrayList<>();
        }
        this.serverIds.add(serverId);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
