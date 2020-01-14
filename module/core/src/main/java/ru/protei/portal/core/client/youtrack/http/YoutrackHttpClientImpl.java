package ru.protei.portal.core.client.youtrack.http;

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

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.function.BiFunction;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class YoutrackHttpClientImpl implements YoutrackHttpClient {

    @PostConstruct
    public void init() {
        headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization", "Bearer " + portalConfig.data().youtrack().getAuthToken());
    }

    @Override
    public <T> Result<T> read(String url, Class<T> clazz) {
        return execute((client, headers) -> client.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), clazz))
                .map(ResponseEntity::getBody);
    }

    @Override
    public <T> Result<T> save(String url, String body, Class<T> clazz) {
        return execute((client, headers) -> client.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), clazz))
                .map(ResponseEntity::getBody);
    }

    private <RES> Result<ResponseEntity<RES>> execute(BiFunction<RestTemplate, HttpHeaders, ResponseEntity<RES>> handler) {
        RestTemplateResponseErrorHandler errorHandler = new RestTemplateResponseErrorHandler();
        RestTemplate ytClient = makeClient(errorHandler);

        ResponseEntity<RES> response;

        try {
            response = handler.apply(ytClient, headers);
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

    private RestTemplate makeClient(RestTemplateResponseErrorHandler errorHandler) {
        RestTemplate template = new RestTemplate(Arrays.asList(
                new ByteArrayHttpMessageConverter(),
                new StringHttpMessageConverter(),
                new ResourceHttpMessageConverter(),
                new SourceHttpMessageConverter<>(),
                new AllEncompassingFormHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter(YtDtoObjectMapperProvider.getMapper(fieldsMapper))
        ));
        if (errorHandler != null) {
            template.setErrorHandler(errorHandler);
        }
        ((DefaultUriTemplateHandler) template.getUriTemplateHandler()).setStrictEncoding(true);
        return template;
    }

    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YtDtoFieldsMapper fieldsMapper;

    private HttpHeaders headers;

    private final static Logger log = LoggerFactory.getLogger(YoutrackHttpClientImpl.class);
}
