package ru.protei.portal.core.client.enterprise1c.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.enterprise1c.dto.WorkPersonInfo1C;
import ru.protei.portal.core.model.enterprise1c.query.WorkQuery1C;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Collections;
import java.util.function.BiFunction;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class HttpClient1CWorkImpl implements HttpClient1CWork{

    @PostConstruct
    public void init() {
        headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                        (portalConfig.data().enterprise1C().getWorkLogin() + ":" +
                         portalConfig.data().enterprise1C().getWorkPassword()).getBytes()));
        headers.set("IBSession", "start");
    }

    @Override
    public Result<WorkPersonInfo1C> getProteiWorkPersonInfo(WorkQuery1C query) {
        return makeResult(execute((client, headers) -> client.exchange(portalConfig.data().enterprise1C().getWorkProteiUrl(),
                HttpMethod.POST, new HttpEntity<>(query, headers), Response.class)));
    }

    @Override
    public Result<WorkPersonInfo1C> getProteiStWorkPersonInfo(WorkQuery1C query) {
        return makeResult(execute((client, headers) ->
                client.exchange(portalConfig.data().enterprise1C().getWorkProteiStUrl(),
                        HttpMethod.POST, new HttpEntity<>(query, headers), Response.class)));

    }

    @Override
    public <T> Result<T> read(String url, Class<T> clazz) {
        return execute((client, headers) -> client.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), clazz))
                                                  .map(ResponseEntity::getBody);
    }

    static private Result<WorkPersonInfo1C> makeResult(Result<ResponseEntity<Response>> resultResponse) {
        if (resultResponse.isOk()) {
            Response response = resultResponse.getData().getBody();
            return new Result<>(response.status, response.data, null, null);
        } else {
            return error(resultResponse.getStatus());
        }
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
            return error(En_ResultStatus.REQUEST_1C_NOT_FOUND);
        }

        return ok(response);
    }

    private RestTemplate makeClient() {
        RestTemplate template = new RestTemplate(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        HttpClient client = HttpClients.createDefault();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
        ((DefaultUriTemplateHandler) template.getUriTemplateHandler()).setStrictEncoding(true);
        return template;
    }

    @Autowired
    private PortalConfig portalConfig;

    private HttpHeaders headers;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAutoDetect
    static private class Response {
        @JsonProperty
        En_ResultStatus status;
        @JsonProperty
        WorkPersonInfo1C data;
    }

    static private final  Logger log = LoggerFactory.getLogger(HttpClient1CWorkImpl.class);
}
