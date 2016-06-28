package ru.protei.portal.webui.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by michael on 27.06.16.
 */
@JsonAutoDetect
public class CoreResponse<T> {

    @JsonProperty
    private boolean isOk;

    @JsonProperty
    private String message;

    @JsonProperty
    private String errCode;

    @JsonProperty
    private T data;

    @JsonProperty
    private String redirect;


    public CoreResponse () {
        this.isOk = true;
    }

    public CoreResponse (String errMsg) {
        this.error(errMsg, null);
    }


    public CoreResponse<T> error (String msg, String code) {
        this.message = msg;
        this.errCode = code;
        this.isOk = false;
        return this;
    }

    public CoreResponse<T> redirect (String to) {
        this.redirect = to;
        return this;
    }

    public CoreResponse<T> success (T data) {
        this.data = data;
        this.isOk = true;
        return this;
    }
}
