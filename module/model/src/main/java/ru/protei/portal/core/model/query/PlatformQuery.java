package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.ArrayList;
import java.util.List;

public class PlatformQuery extends BaseQuery {

    private Long platformId;
    private List<Long> companyIds;
    private List<Long> managerIds;
    private String params;
    private String comment;
    private String serverIp;

    public PlatformQuery() {
        this("", En_SortField.id, En_SortDir.ASC);
    }

    public PlatformQuery(String name, En_SortField sortField, En_SortDir sortDir) {
        super(name, sortField, sortDir);
    }

    public PlatformQuery(List<Long> companyIds) {
        this();
        this.companyIds = companyIds;
    }

    public static PlatformQuery forId(Long platformId) {
        PlatformQuery query = new PlatformQuery();
        query.setPlatformId(platformId);
        return query;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
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

    public List<Long> getManagerIds() {
        return managerIds;
    }

    public void setManagerIds(List<Long> managerIds) {
        this.managerIds = managerIds;
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

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }
}
