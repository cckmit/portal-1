package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerQuery extends BaseQuery {

    private Long serverId;
    private List<Long> platformIds;
    private String ip;
    private String params;
    private String comment;

    public ServerQuery() {
        this("", En_SortField.id, En_SortDir.ASC);
    }

    public ServerQuery(String name, En_SortField sortField, En_SortDir sortDir) {
        super(name, sortField, sortDir);
        this.platformIds = new ArrayList<>();
    }

    public static ServerQuery forId(Long serverId) {
        ServerQuery query = new ServerQuery();
        query.setServerId(serverId);
        return query;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public List<Long> getPlatformIds() {
        return platformIds;
    }

    public void setPlatformIds(List<Long> platformIds) {
        this.platformIds = platformIds != null ? platformIds : new ArrayList<>();
    }

    public void setPlatformId(Long platformId) {
        if (platformId == null) {
            this.platformIds.clear();
            return;
        }
        this.platformIds = Collections.singletonList(platformId);
    }

    public void addPlatformId(Long platformId) {
        this.platformIds.add(platformId);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
