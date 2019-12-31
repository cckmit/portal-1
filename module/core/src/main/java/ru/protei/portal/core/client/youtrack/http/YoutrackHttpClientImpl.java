package ru.protei.portal.core.client.youtrack.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoObjectMapperProvider;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.api.YtDto;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public <RES> Result<RES> read(String url, Class<RES> clazz) {
        return read(url, "", clazz);
    }

    @Override
    public <RES> Result<RES> read(String url, String query, Class<RES> clazz) {
        String fields = fieldsMapper.getFields(clazz);
        return read(url, fields, query, clazz);
    }

    @Override
    public <RES> Result<RES> read(String url, String fields, String query, Class<RES> clazz) {
        String path = buildUrl(url, fields, query);
        return execute((client, headers) -> client.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), clazz))
                .map(ResponseEntity::getBody);
    }

    @Override
    public <REQ extends YtDto, RES> Result<RES> save(String url, Class<RES> clazz, REQ dto) {
        return save(url, clazz, dto, new String[0]);
    }

    @Override
    public <REQ extends YtDto, RES> Result<RES> save(String url, Class<RES> clazz, REQ dto, String...dtoForceIncludeFields) {
        String fields = fieldsMapper.getFields(clazz);
        return save(url, fields, clazz, dto, dtoForceIncludeFields);
    }

    @Override
    public <REQ extends YtDto, RES> Result<RES> save(String url, String fields, Class<RES> clazz, REQ dto) {
        return save(url, fields, clazz, dto, new String[0]);
    }

    @Override
    public <REQ extends YtDto, RES> Result<RES> save(String url, String fields, Class<RES> clazz, REQ dto, String...dtoForceIncludeFields) {
        try {
            String path = buildUrl(url, fields, "");
            String body = serializeDto(dto, dtoForceIncludeFields);
            return execute((client, headers) -> client.postForEntity(path, new HttpEntity<>(body, headers), clazz))
                    .map(ResponseEntity::getBody);
        } catch (Exception e) {
            log.warn("save(): Failed to do save request", e);
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    @Override
    public <RES> Result<ResponseEntity<RES>> execute(BiFunction<RestTemplate, HttpHeaders, ResponseEntity<RES>> handler) {
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

    public void setAuthHeaders(HttpHeaders authHeaders) {
        this.authHeaders = authHeaders;
    }

    private String buildUrl(String base, String fields, String query) {
        List<String> queryList = new ArrayList<>();
        try {
            if (StringUtils.isNotEmpty(fields)) queryList.add("fields=" + URLEncoder.encode(fields, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.warn("Unable to append fields to url", e);
        }
        try {
            if (StringUtils.isNotEmpty(query)) queryList.add("query=" + URLEncoder.encode(query, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.warn("Unable to append query to url", e);
        }
        String url = base;
        if (CollectionUtils.isNotEmpty(queryList)) {
            url += "?" + String.join("&", queryList);
        }
        return url;
    }

    private RestTemplate makeClient(RestTemplateResponseErrorHandler errorHandler) {
        RestTemplate template = new RestTemplate();
        defineCustomObjectMapper(template);
        if (errorHandler != null) {
            template.setErrorHandler(errorHandler);
        }
        ((DefaultUriTemplateHandler) template.getUriTemplateHandler()).setStrictEncoding(true);
        return template;
    }

    private RestTemplate defineCustomObjectMapper(RestTemplate template) {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        template.getMessageConverters().removeIf(m -> MappingJackson2HttpMessageConverter.class.getName().equals(m.getClass().getName()));
        template.getMessageConverters().add(messageConverter);
        return template;
    }

    private <T extends YtDto> String serializeDto(T dto, String...forceIncludeFields) throws JsonProcessingException {
        return objectMapper
            .writer(YtDtoObjectMapperProvider.getFilterProvider(Arrays.asList(forceIncludeFields)))
            .writeValueAsString(dto);
    }

    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YtDtoFieldsMapper fieldsMapper;

    private HttpHeaders authHeaders;
    private ObjectMapper objectMapper;

    private final static Logger log = LoggerFactory.getLogger(YoutrackHttpClientImpl.class);
}

