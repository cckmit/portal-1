package ru.protei.portal.core.client.youtrack.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.protei.portal.api.struct.Result;

import java.util.function.BiFunction;

public interface YoutrackHttpClient {
    <T> Result<T> read( String url, Class<T> returnObjectClass );

    <T> Result<T> create( String url, Class<T> returnObjectClass );

    <T> Result<T> update( String url, Class<T> returnObjectClass );

    <T, BodyObject> Result<T> update( String url, Class<T> returnObjectClass, BodyObject requestBodyObject);

    <T> Result<ResponseEntity<T>> execute( BiFunction<RestTemplate, HttpHeaders, ResponseEntity<T>> work );
}
