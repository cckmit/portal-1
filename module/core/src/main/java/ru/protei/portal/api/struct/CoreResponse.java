package ru.protei.portal.api.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by michael on 27.06.16.
 */
@JsonAutoDetect
public class CoreResponse<T> {

    @JsonProperty
    private boolean ok;

    @JsonProperty
    private String message;

    @JsonProperty
    private String errCode;

    @JsonProperty
    private T data;

    @JsonProperty
    private String redirect;


    public CoreResponse () {
        this.ok = true;
    }

    public CoreResponse (String errMsg) {
        this.error(errMsg, null);
    }


    public boolean isOk () {
        return ok;
    }

    public boolean isError () {
        return !ok;
    }

    public String getMessage() {
        return message;
    }

    public String getErrCode () {
        return errCode;
    }

    public T getData () {
        return data;
    }

    public CoreResponse<T> error (String msg, String code) {
        this.message = msg;
        this.errCode = code;
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
