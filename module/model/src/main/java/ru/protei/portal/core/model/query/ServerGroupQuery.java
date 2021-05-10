package ru.protei.portal.core.model.query;

public class ServerGroupQuery extends BaseQuery {
    private Long platformId;

    public ServerGroupQuery() {}

    public ServerGroupQuery(Long platformId) {
        this.platformId = platformId;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    @Override
    public String toString() {
        return "ServerGroupQuery{" +
                "searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                ", platformId=" + platformId +
                '}';
    }
}
