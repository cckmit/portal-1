package ru.protei.portal.core.client.youtrack.http;

import ru.protei.portal.api.struct.Result;

public interface YoutrackHttpClient {

    <T> Result<T> read(String url, Class<T> clazz);

    <T> Result<T> save(String url, String body, Class<T> clazz);
}
