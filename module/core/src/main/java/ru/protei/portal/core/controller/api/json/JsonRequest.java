package ru.protei.portal.core.controller.api.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = Visibility.NONE, 
        getterVisibility = Visibility.NONE, 
        setterVisibility = Visibility.NONE
)
public class JsonRequest<T> implements Serializable {
    @JsonProperty
    private String requestId;
    @JsonProperty
    private T data;

    public JsonRequest() {}

    public JsonRequest(String requestId, T data) {
        this.data = data;
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
