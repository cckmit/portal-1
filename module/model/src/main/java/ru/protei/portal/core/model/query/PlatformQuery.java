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
        this.companyIds = companyIds;
    }

    public void setCompanyId(Long companyId) {
        this.companyIds = Collections.singletonList(companyId);
    }

    public void addCompanyId(Long companyId) {
        if (this.companyIds == null) {
            this.companyIds = new ArrayList<>();
        }
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
