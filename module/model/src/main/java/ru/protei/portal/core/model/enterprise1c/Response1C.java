package ru.protei.portal.core.model.enterprise1c;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response1C <T> {

    @JsonProperty("odata.metadata")
    String metadata;

    @JsonProperty("value")
    T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
