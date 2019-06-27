package ru.protei.portal.core.controller.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ResultStatus;

/**
 *  Результат выполнения API запроса
 */
public class APIResult<T>
{
    @JsonProperty("ResultStatus")
    private String status;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Data")
    private T data;

    public APIResult() {
        status = En_ResultStatus.OK.name();
    }

    public APIResult(T data) {
        this.data = data;
        status = En_ResultStatus.OK.name();
    }

    public APIResult(String status) {
        this.status = status;
        message  = (status == En_ResultStatus.OK.name()) ? "" : "Error : " + status;
    }

    public APIResult(String status, String message) {
        this.status = status;
        this.message = (message != null) ? message : "Error : " + status;
    }

/*    public APIResult(En_ResultStatus resultStatus) {
        this.status = resultStatus.name();
        this.message     = (resultStatus.getMessage() != null) ? resultStatus.getMessage() : "Error code: " + result.getCode();
    }*/

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

    @JsonIgnore
    public boolean isResult (En_ResultStatus resultStatus) { return this.status == resultStatus.name(); }

    public static <T> APIResult<T> okWithData (T data) { return new APIResult<>(data); }

/*    @SuppressWarnings("unchecked")
    public static <T> APIResult<T> anyError (SLResult<?> slResult, Locale locale) {
        String msg = slResult.getErrorMessage(locale);
        return new APIResult(slResult.getResultCode(), msg);
    }*/

/*    @SuppressWarnings("unchecked")
    public static <T> APIResult<T> error (En_ResultStatus resultStatus) {
        return new APIResult<>(resultStatus);
    }*/

    @SuppressWarnings("unchecked")
    public static <T> APIResult<T> error (En_ResultStatus resultStatus, String msg) {
        return new APIResult<>(resultStatus, msg);
    }

    @SuppressWarnings("unchecked")
    public static <T> APIResult<T> notImplemented () {
        return new APIResult(En_ResultStatus.NOT_AVAILABLE, "method is not implemented");
    }

    @SuppressWarnings("unchecked")
    public static <T> APIResult<T> startDateInvalid () {
        return new APIResult(En_ResultStatus.INCORRECT_PARAMS, "start date is invalid");
    }

    @SuppressWarnings("unchecked")
    public static <T> APIResult<T> endDateInvalid () {
        return new APIResult(En_ResultStatus.INCORRECT_PARAMS, "end date is invalid");
    }
}
