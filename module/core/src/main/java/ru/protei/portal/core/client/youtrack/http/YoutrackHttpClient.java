package ru.protei.portal.core.client.youtrack.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.yt.api.YtDto;

import java.util.function.BiFunction;

public interface YoutrackHttpClient {

    <RES extends YtDto> Result<RES> read(String url, Class<RES> clazz);

    <RES extends YtDto> Result<RES> read(String url, String fields, Class<RES> clazz);

    <REQ extends YtDto, RES extends YtDto> Result<RES> save(String url, Class<RES> clazz, REQ dto);

    <REQ extends YtDto, RES extends YtDto> Result<RES> save(String url, Class<RES> clazz, REQ dto, String...dtoForceIncludeFields);

    <REQ extends YtDto, RES extends YtDto> Result<RES> save(String url, String fields, Class<RES> clazz, REQ dto);

    <REQ extends YtDto, RES extends YtDto> Result<RES> save(String url, String fields, Class<RES> clazz, REQ dto, String...dtoForceIncludeFields);

    <RES> Result<ResponseEntity<RES>> execute(BiFunction<RestTemplate, HttpHeaders, ResponseEntity<RES>> handler);
}
