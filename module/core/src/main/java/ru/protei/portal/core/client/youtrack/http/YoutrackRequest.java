package ru.protei.portal.core.client.youtrack.http;

import ru.protei.portal.core.model.youtrack.YtFieldDescriptor;

import java.util.HashMap;
import java.util.Map;

public class YoutrackRequest<REQ, RES> {

    private String url;
    private Map<String, String> params;
    private Class<RES> response;
    private Class<?>[] responseIncludeYtDtoFields;
    private boolean responseIncludeNotYtDtoFields;
    private REQ request;
    private YtFieldDescriptor[] requestFieldsToRemove;

    public YoutrackRequest(Class<RES> clazz) {
        this(null, clazz);
    }

    public YoutrackRequest(String url, Class<RES> clazz) {
        this.url = url;
        this.response = clazz;
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

    public YoutrackRequest<REQ, RES> fillResponseWithPojo() {
        this.responseIncludeNotYtDtoFields = true;
        return this;
    }

    public YoutrackRequest<REQ, RES> fillResponseWithYt(Class<?>...classes) {
        this.responseIncludeYtDtoFields = classes;
        return this;
    }

    public YoutrackRequest<REQ, RES> save(REQ dto) {
        this.request = dto;
        return this;
    }

    public YoutrackRequest<REQ, RES> remove(REQ dto, YtFieldDescriptor...fieldNamesToRemove) {
        this.request = dto;
        this.requestFieldsToRemove = fieldNamesToRemove;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Class<RES> getResponse() {
        return response;
    }

    public Class<?>[] getResponseIncludeYtDtoFields() {
        return responseIncludeYtDtoFields;
    }

    public boolean getResponseIncludeNotYtDtoFields() {
        return responseIncludeNotYtDtoFields;
    }

    public REQ getRequest() {
        return request;
    }

    public YtFieldDescriptor[] getRequestFieldsToRemove() {
        return requestFieldsToRemove;
    }
}
