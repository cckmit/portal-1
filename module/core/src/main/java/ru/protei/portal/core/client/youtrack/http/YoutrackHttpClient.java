package ru.protei.portal.core.client.youtrack.http;

import ru.protei.portal.api.struct.Result;

import java.util.Map;

public interface YoutrackHttpClient {

    <RES> Result<RES> read(YoutrackRequest<?, RES> request);

    <REQ, RES> Result<RES> create(YoutrackRequest<REQ, RES> request);

    <REQ, RES> Result<RES> update(YoutrackRequest<REQ, RES> request);

    <REQ, RES> Result<RES> remove(YoutrackRequest<REQ, RES> request);
}
