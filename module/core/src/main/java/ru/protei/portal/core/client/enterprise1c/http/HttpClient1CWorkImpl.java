package ru.protei.portal.core.client.enterprise1c.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.enterprise1c.dto.WorkPersonInfo1C;
import ru.protei.portal.core.model.enterprise1c.query.WorkQuery1C;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class HttpClient1CWorkImpl implements HttpClient1CWork{

    @PostConstruct
    public void init() {
        headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((portalConfig.data().enterprise1C().getWorkLogin() + ":" + portalConfig.data().enterprise1C().getWorkPassword()).getBytes()));
        headers.set("IBSession", "start");
    }

    @Override
    public Result<WorkPersonInfo1C> getProteiWorkPersonInfo(WorkQuery1C query) {
        return execute((client, headers) -> client.exchange(portalConfig.data().enterprise1C().getWorkProteiUrl(),
                HttpMethod.POST, new HttpEntity<>(query, headers), WorkPersonInfo1C.class))
                .map(ResponseEntity::getBody);
    }

    @Override
    public Result<WorkPersonInfo1C> getProteiStWorkPersonInfo(WorkQuery1C query) {
        return execute((client, headers) -> client.exchange(portalConfig.data().enterprise1C().getWorkProteiStUrl(),
                HttpMethod.POST, new HttpEntity<>(query, headers), WorkPersonInfo1C.class))
                .map(ResponseEntity::getBody);
    }

    private <RES> Result<ResponseEntity<RES>> execute(BiFunction<RestTemplate, HttpHeaders, ResponseEntity<RES>> handler) {
        RestTemplate client1C = makeClient();
        ResponseEntity<RES> response;

        try {
            response = handler.apply(client1C, headers);
        } catch (Exception e) {
            log.warn("execute(): Can't execute 1c request, unexpected exception", e);
            return error(En_ResultStatus.REQUEST_1C_FAILED);
        }
        if (response == null) {
            log.warn("execute(): Can't execute 1c request, response is null");
            return error(En_ResultStatus.REQUEST_1C_FAILED);
        }
        if (HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
            log.warn("execute(): Can't get data from 1c, NOT_FOUND");
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(response);
    }

    private RestTemplate makeClient() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);

        RestTemplate template = new RestTemplate(messageConverters);
        template.setMessageConverters(messageConverters);

        HttpClient client = HttpClients.createDefault();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
        ((DefaultUriTemplateHandler) template.getUriTemplateHandler()).setStrictEncoding(true);
        return template;
    }

    @Autowired
    private PortalConfig portalConfig;

    private HttpHeaders headers;

    static private final  Logger log = LoggerFactory.getLogger(HttpClient1CWorkImpl.class);
}
