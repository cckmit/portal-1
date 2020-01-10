package ru.protei.portal.core.client.youtrack.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.yt.dto.YtDto;

import java.util.Map;
import java.util.function.BiFunction;

public interface YoutrackHttpClient {

    <RES> Result<RES> read(String url, Class<RES> clazz);

    <RES> Result<RES> read(String url, String query, Class<RES> clazz);

    <RES> Result<RES> read(String url, Map<String, String> params, Class<RES> clazz);

    <RES> Result<RES> read(String url, String fields, String query, Class<RES> clazz);

    <RES> Result<RES> read(String url, String fields, Map<String, String> params, Class<RES> clazz);

    <REQ extends YtDto, RES> Result<RES> save(String url, Class<RES> clazz, REQ dto);

    <REQ extends YtDto, RES> Result<RES> save(String url, Class<RES> clazz, REQ dto, String...dtoForceIncludeFields);

    <REQ extends YtDto, RES> Result<RES> save(String url, String fields, Class<RES> clazz, REQ dto);

    <REQ extends YtDto, RES> Result<RES> save(String url, String fields, Class<RES> clazz, REQ dto, String...dtoForceIncludeFields);

    <RES> Result<ResponseEntity<RES>> execute(BiFunction<RestTemplate, HttpHeaders, ResponseEntity<RES>> handler);
}
