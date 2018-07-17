package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlatformQuery extends BaseQuery {

    private Long platformId;
    private List<Long> companyIds;
    private String params;
    private String comment;

    public PlatformQuery() {
        this("", En_SortField.id, En_SortDir.ASC);
    }

    public PlatformQuery(String name, En_SortField sortField, En_SortDir sortDir) {
        super(name, sortField, sortDir);
        this.companyIds = new ArrayList<>();
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
        this.companyIds = Collections.singletonList(companyId);
    }

    public void addCompanyId(Long companyId) {
        this.companyIds.add(companyId);
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
