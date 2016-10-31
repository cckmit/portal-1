package ru.protei.portal.api.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ResultStatus;

/**
 * Created by michael on 27.06.16.
 */
@JsonAutoDetect
public class CoreResponse<T> {

    @JsonProperty
    private boolean ok;

    @JsonProperty
    private En_ResultStatus status;

    @JsonProperty
    private T data;

    @JsonProperty
    private String redirect;


    public CoreResponse () {
        this.ok = true;
    }

    public boolean isOk () {
        return ok;
    }

    public boolean isError () {
        return !ok;
    }


    public En_ResultStatus getStatus () {
        return status;
    }

    public T getData () {
        return data;
    }

    public CoreResponse<T> error (En_ResultStatus status) {
        this.status = status;
        this.ok = false;
        return this;
    }

    public CoreResponse<T> redirect (String to) {
        this.redirect = to;
        return this;
    }

    public CoreResponse<T> success (T data) {
        this.data = data;
        this.ok = true;
        return this;
    }
}
