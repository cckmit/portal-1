package ru.protei.portal.core.controller.api.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class JsonResponse<T> implements Serializable {
    @JsonProperty
    private String requestId;
    @JsonProperty
    private En_ResultStatus status;
    @JsonProperty
    private T data;
    @JsonProperty
    private String message;
    
    public JsonResponse() {}

    public JsonResponse(String requestId, En_ResultStatus status, T data, String message) {
        this.requestId = requestId;
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public JsonResponse(String requestId, Result<T> result) {
        this.requestId = requestId;
        this.status = result.getStatus();
        this.data = result.getData();
        this.message = result.getMessage();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public En_ResultStatus getStatus() {
        return status;
    }

    public void setStatus(En_ResultStatus status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
