package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.ArrayList;
import java.util.List;

public class ServerQuery extends BaseQuery {

    private Long serverId;
    private List<Long> companyIds;
    private List<Long> platformIds;
    private String ip;
    private String params;
    private String comment;
    private String nameOrIp;

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

    public static ServerQuery forPlatformId(Long platformId) {
        ServerQuery query = new ServerQuery();
        query.setPlatformId(platformId);
        return query;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds != null ? companyIds : new ArrayList<>();
    }

    public void setCompanyId(Long companyId) {
        if (companyId == null) {
            this.companyIds.clear();
            return;
        }
        this.companyIds = new ArrayList<>();
        this.companyIds.add(companyId);
    }

    public void addCompanyId(Long companyId) {
        this.companyIds.add(companyId);
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
        this.platformIds = new ArrayList<>();
        this.platformIds.add(platformId);
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

    public String getNameOrIp() {
        return nameOrIp;
    }

    public void setNameOrIp(String nameOrIp) {
        this.nameOrIp = nameOrIp;
    }
}
