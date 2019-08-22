package ru.protei.portal.core.client.youtrack.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.protei.portal.api.struct.CoreResponse;

import java.util.function.BiFunction;

public interface YoutrackHttpClient {
    <T> CoreResponse<T> read( String url, Class<T> returnObjectClass );

    <T> CoreResponse<T> create( String url, Class<T> returnObjectClass );

    <T> CoreResponse<T> update( String url, Class<T> returnObjectClass );

    <T, BodyObject> CoreResponse<T> update( String url, Class<T> returnObjectClass, BodyObject requestBodyObject);

    <T> CoreResponse<ResponseEntity<T>> execute( BiFunction<RestTemplate, HttpHeaders, ResponseEntity<T>> work );
}
