package ru.protei.portal.core.client.youtrack.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoObjectMapperProvider;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.youtrack.YtFieldDescriptor;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.BiFunction;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class YoutrackHttpClientImpl implements YoutrackHttpClient {

    @PostConstruct
    public void init() {
        authHeaders = new HttpHeaders();
        authHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        authHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        authHeaders.set("Authorization", "Bearer " + portalConfig.data().youtrack().getAuthToken());
        objectMapper = YtDtoObjectMapperProvider.getMapper(fieldsMapper);
    }

    @Override
    public <RES> Result<RES> read(YoutrackRequest<?, RES> request) {
        ensureFieldsParamSet(request);
        return doRead(request.getUrl(), request.getParams(), request.getResponseClass());
    }

    @Override
    public <REQ, RES> Result<RES> create(YoutrackRequest<REQ, RES> request) {
        ensureFieldsParamSet(request);
        return doSave(request.getUrl(), request.getParams(), request.getResponseClass(), request.getRequestDto());
    }

    @Override
    public <REQ, RES> Result<RES> update(YoutrackRequest<REQ, RES> request) {
        ensureFieldsParamSet(request);
        return doSave(request.getUrl(), request.getParams(), request.getResponseClass(), request.getRequestDto());
    }

    @Override
    public <REQ, RES> Result<RES> remove(YoutrackRequest<REQ, RES> request) {
        ensureFieldsParamSet(request);
        return doSave(request.getUrl(), request.getParams(), request.getResponseClass(), request.getRequestDto(), request.getRequestDtoFieldNamesToRemove());
    }

    private void ensureFieldsParamSet(YoutrackRequest<?, ?> request) {
        if (!request.getParams().containsKey("fields")) {
            String fields = fieldsMapper.getFields(request.getResponseClass(), request.getResponseClassIncludeClasses());
            request.getParams().put("fields", fields);
        }
    }

    private <RES> Result<RES> doRead(String url, Map<String, String> params, Class<RES> clazz) {
        return buildUrl(url, params)
            .flatMap(path -> execute((client, headers) -> client.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), clazz)))
            .map(ResponseEntity::getBody);
    }

    private <REQ, RES> Result<RES> doSave(String url, Map<String, String> params, Class<RES> clazz, REQ dto, YtFieldDescriptor...dtoForceIncludeFields) {
        return buildUrl(url, params)
            .flatMap(path -> serializeDto(dto, dtoForceIncludeFields)
                .flatMap(body -> execute((client, headers) -> client.postForEntity(path, new HttpEntity<>(body, headers), clazz)))
                .map(ResponseEntity::getBody)
            );
    }

    private <RES> Result<ResponseEntity<RES>> execute(BiFunction<RestTemplate, HttpHeaders, ResponseEntity<RES>> handler) {
        RestTemplateResponseErrorHandler errorHandler = new RestTemplateResponseErrorHandler();
        RestTemplate ytClient = makeClient(errorHandler);

        ResponseEntity<RES> response;

        try {
            response = handler.apply(ytClient, authHeaders);
        } catch (Exception e) {
            log.warn("execute(): Can't execute youtrack request, unexpected exception", e);
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        if (response == null) {
            log.warn("execute(): Can't execute youtrack request, response is null");
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        if (HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
            log.warn("execute(): Can't get data from youtrack, NOT_FOUND");
            return error(En_ResultStatus.NOT_FOUND);
        }
        if (!errorHandler.isOk()) {
            if (HttpStatus.NOT_FOUND.equals(errorHandler.getStatus())) {
                log.warn("execute(): Can't get data from youtrack, request failed with status NOT_FOUND. message: {}", response.getBody());
                return error(En_ResultStatus.NOT_FOUND);
            }
            log.warn("execute(): Can't execute youtrack request, request failed with status {}. message: {}", errorHandler.getStatus(), response.getBody());
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        return ok(response);
    }

    private Result<String> buildUrl(String base, Map<String, String> params) {
        if (!params.containsKey("fields")) {
            return error(En_ResultStatus.INTERNAL_ERROR, "No 'fields' value provided");
        }
        List<String> queryList = new ArrayList<>();
        for (Map.Entry<String, String> entry : CollectionUtils.emptyIfNull(params).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                queryList.add(key + "=" + urlEncode(value));
            }
        }
        String url = base;
        if (CollectionUtils.isNotEmpty(queryList)) {
            url += "?" + String.join("&", queryList);
        }
        return ok(url);
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("urlEncode(): Failed to urlencode (" + value + ")", e);
            return value;
        }
    }

    private RestTemplate makeClient(RestTemplateResponseErrorHandler errorHandler) {
        RestTemplate template = new RestTemplate(Arrays.asList(
                new MappingJackson2HttpMessageConverter(objectMapper),
                new ByteArrayHttpMessageConverter(),
                new StringHttpMessageConverter(),
                new ResourceHttpMessageConverter(),
                new SourceHttpMessageConverter<>(),
                new AllEncompassingFormHttpMessageConverter()
        ));
        if (errorHandler != null) {
            template.setErrorHandler(errorHandler);
        }
        ((DefaultUriTemplateHandler) template.getUriTemplateHandler()).setStrictEncoding(true);
        return template;
    }

    private <T> Result<String> serializeDto(T dto, YtFieldDescriptor...forceIncludeFields) {
        try {
            String body = objectMapper
                .writer(YtDtoObjectMapperProvider.getFilterProvider(Arrays.asList(forceIncludeFields)))
                .writeValueAsString(dto);
            return ok(body);
        } catch (JsonProcessingException e) {
            log.error(String.format("Failed to serialize dto : [%s]", dto), e);
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YtDtoFieldsMapper fieldsMapper;

    private HttpHeaders authHeaders;
    private ObjectMapper objectMapper;

    private final static Logger log = LoggerFactory.getLogger(YoutrackHttpClientImpl.class);
}

