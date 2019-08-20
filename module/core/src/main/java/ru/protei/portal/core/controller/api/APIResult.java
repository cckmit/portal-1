package ru.protei.portal.core.controller.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ResultStatus;

/**
 *  Результат выполнения API запроса
 */
@JsonAutoDetect
public class APIResult<T>
{
    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    public APIResult() {
        status = En_ResultStatus.OK.name();
    }

    public APIResult(T data) {
        this.data = data;
        status = En_ResultStatus.OK.name();
        message = "";
    }

    public APIResult(String status) {
        this.status = status;
        message  = (status == En_ResultStatus.OK.name()) ? "" : "Error : " + status;
    }

    public APIResult(String status, String message) {
        this.status = status;
        this.message = (message != null) ? message : "Error : " + status;
    }

    public APIResult(En_ResultStatus resultStatus, String message) {
        this.status = resultStatus.name();
        this.message = (message != null) ? message : "Error code: " + status;
    }

    @JsonIgnore
    public T getData() {
        return data;
    }

    @JsonIgnore
    public String getStatus()
    {
        return status;
    }

    @JsonIgnore
    public void setStatus(String status) { this.status = status; }

    @JsonIgnore
    public String getMessage()
    {
        return message;
    }

    @JsonIgnore
    public void setMessage(String message)
    {
        this.message = message;
    }

    @JsonIgnore
    public boolean isOk() { return status == En_ResultStatus.OK.name(); }

    @JsonIgnore
    public boolean isFail () {
        return !isOk();
    }

    public static <T> APIResult<T> okWithData (T data) { return new APIResult<>(data); }

    @SuppressWarnings("unchecked")
    public static <T> APIResult<T> error (En_ResultStatus resultStatus, String msg) {
        return new APIResult<>(resultStatus, msg);
    }

    public static <T> APIResult<T> error (String status, String msg) {
        return new APIResult<>(status, msg);
    }
}
