package ru.protei.portal.core.client.enterprise1c.http;

import ru.protei.portal.api.struct.Result;

public interface HttpClient1C {

    <T> Result<T> read(String url, Class<T> clazz);

    <T> Result<T> save(String url, String body, Class<T> clazz);

    <T> Result<T> update(String url, String body, Class<T> clazz);
}
