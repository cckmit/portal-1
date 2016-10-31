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
    private En_ResultStatus status;

    @JsonProperty
    private T data;

    public boolean isOk () {
        return status == En_ResultStatus.OK;
    }

    public boolean isError () {
        return status != En_ResultStatus.OK;
    }

    public En_ResultStatus getStatus () {
        return status;
    }

    public T getData () {
        return data;
    }

    public CoreResponse<T> error (En_ResultStatus status) {
        this.status = status;
        return this;
    }

    public CoreResponse<T> redirect (String to) {
        return this;
    }

    public CoreResponse<T> success (T data) {
        this.data = data;
        this.status = En_ResultStatus.OK;
        return this;
    }
}
