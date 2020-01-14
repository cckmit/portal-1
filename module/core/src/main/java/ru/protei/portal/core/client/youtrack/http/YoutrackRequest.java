package ru.protei.portal.core.client.youtrack.http;

import ru.protei.portal.core.model.youtrack.YtFieldDescriptor;

import java.util.HashMap;
import java.util.Map;

public class YoutrackRequest<REQ, RES> {

    private String url;
    private Map<String, String> params;
    private Class<RES> responseClass;
    private Class<?>[] responseClassIncludeClasses;
    private REQ requestDto;
    private YtFieldDescriptor[] requestDtoFieldNamesToRemove;

    public YoutrackRequest(Class<RES> clazz) {
        this(null, clazz);
    }

    public YoutrackRequest(String url, Class<RES> clazz) {
        this.url = url;
        this.responseClass = clazz;
        this.params = new HashMap<>();
    }

    public YoutrackRequest<REQ, RES> url(String url) {
        this.url = url;
        return this;
    }

    public YoutrackRequest<REQ, RES> fields(String fields) {
        this.params.put("fields", fields);
        return this;
    }

    public YoutrackRequest<REQ, RES> query(String query) {
        this.params.put("query", query);
        return this;
    }

    public YoutrackRequest<REQ, RES> params(Map<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    public YoutrackRequest<REQ, RES> fillResponseWith(Class<?>...classes) {
        this.responseClassIncludeClasses = classes;
        return this;
    }

    public YoutrackRequest<REQ, RES> save(REQ dto) {
        this.requestDto = dto;
        return this;
    }

    public YoutrackRequest<REQ, RES> remove(REQ dto, YtFieldDescriptor...fieldNamesToRemove) {
        this.requestDto = dto;
        this.requestDtoFieldNamesToRemove = fieldNamesToRemove;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Class<RES> getResponseClass() {
        return responseClass;
    }

    public Class<?>[] getResponseClassIncludeClasses() {
        return responseClassIncludeClasses;
    }

    public REQ getRequestDto() {
        return requestDto;
    }

    public YtFieldDescriptor[] getRequestDtoFieldNamesToRemove() {
        return requestDtoFieldNamesToRemove;
    }
}
