package ru.protei.portal.core.client.enterprise1c.http;

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
import ru.protei.portal.core.model.dict.En_ResultStatus;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.function.BiFunction;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class HttpClient1CImpl implements HttpClient1C{

    @PostConstruct
    public void init() {
        headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((portalConfig.data().enterprise1C().getLogin() + ":" + portalConfig.data().enterprise1C().getPassword()).getBytes()));
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
        RestTemplate client1C = makeClient();
        ResponseEntity<RES> response;

        try {
            response = handler.apply(client1C, headers);
        } catch (Exception e) {
            log.warn("execute(): Can't execute 1c request, unexpected exception", e);
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        if (response == null) {
            log.warn("execute(): Can't execute 1c request, response is null");
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        if (HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
            log.warn("execute(): Can't get data from 1c, NOT_FOUND");
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(response);
    }

    private RestTemplate makeClient() {
        RestTemplate template = new RestTemplate(Arrays.asList(
                new ByteArrayHttpMessageConverter(),
                new StringHttpMessageConverter(),
                new ResourceHttpMessageConverter(),
                new SourceHttpMessageConverter<>(),
                new AllEncompassingFormHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter()
        ));
        ((DefaultUriTemplateHandler) template.getUriTemplateHandler()).setStrictEncoding(true);
        return template;
    }

    @Autowired
    private PortalConfig portalConfig;

    private HttpHeaders headers;

    private final static Logger log = LoggerFactory.getLogger(HttpClient1CImpl.class);
}
