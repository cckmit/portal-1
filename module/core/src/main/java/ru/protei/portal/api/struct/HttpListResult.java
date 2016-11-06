package ru.protei.portal.api.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by michael on 06.04.16.
 *
 * @deprecated Use CoreResponse instead
 */
@JsonAutoDetect
@Deprecated
public class HttpListResult<T> {

    @JsonProperty("size")
    private int totalSize;

    @JsonProperty("more")
    private boolean hasMore;

    @JsonProperty("items")
    public List<T> items;


    public HttpListResult () {
        this.items = null;
        this.totalSize = 0;
        this.hasMore = false;
    }

    public HttpListResult (List<T> items, boolean more) {
        this.items = items;
        this.totalSize = items.size();
        this.hasMore = more;
    }

    public List<T> getItems () {
        return items;
    }

    public int getTotalSize() {
        return totalSize;
    }
}
