package ru.protei.portal.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;
import static ru.protei.portal.api.struct.CoreResponse.errorSt;
import static ru.protei.portal.api.struct.CoreResponse.ok;

/**
 *
 */
public class YoutrackHttpClientImpl implements YoutrackHttpClient {

    @PostConstruct
    public void initAuthHeadersAndUrl() {
        authHeaders = new HttpHeaders();
        authHeaders.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );
        authHeaders.setContentType( MediaType.APPLICATION_JSON_UTF8  );
        authHeaders.set( "Authorization", "Bearer " + portalConfig.data().youtrack().getAuthToken() );
    }

    @Override
    public <T> CoreResponse<T> read( String url, Class<T> clazz ) {
        return execute( (ytClient, headers) ->
                ytClient.exchange( url, HttpMethod.GET, new HttpEntity<>( headers ), clazz ) ).map(
                ResponseEntity::getBody );
    }

    @Override
    public <T> CoreResponse<T> create( String url, Class<T> clazz ) {
        return execute((ytClient, headers) ->
                ytClient.exchange( url, HttpMethod.PUT, new HttpEntity<>( headers ), clazz ) ).map(
                ResponseEntity::getBody );
    }

    @Override
    public <T> CoreResponse<T> update( String url, Class<T> clazz ) {
        return execute( (ytClient, headers) ->
                ytClient.exchange( url, HttpMethod.POST, new HttpEntity<>( headers ), clazz ) ).map(
                ResponseEntity::getBody );
    }

    @Override
    public <T, BodyObject> CoreResponse<T> update( String url, Class<T> clazz, BodyObject body ) {
        return execute( ( ytClient, headers ) ->
                ytClient.postForEntity( url, new HttpEntity<>( body, headers ), clazz ) ).map(
                ResponseEntity::getBody );
    }

    @Override
    public <T> CoreResponse<ResponseEntity<T>> execute( BiFunction<RestTemplate, HttpHeaders, ResponseEntity<T>> work ) {
        RestTemplateResponseErrorHandler errorHandler = new RestTemplateResponseErrorHandler();
        RestTemplate ytClient = makeClient( errorHandler );

        ResponseEntity<T> response;

        try {
            response = work.apply( ytClient, authHeaders );
        } catch (Exception e) {
            log.warn( "execute(): Can't execute youtrack request, unexpected exception: {}", e );
            return errorSt( En_ResultStatus.GET_DATA_ERROR );
        }
        if (response == null) {
            log.warn( "execute(): Can't execute youtrack request, result is null" );
            return errorSt( En_ResultStatus.GET_DATA_ERROR );
        }
        if (HttpStatus.NOT_FOUND.equals( response.getStatusCode() )) {
            log.warn( "execute(): Can't get data from youtrack, NOT_FOUND. " );
            return errorSt( En_ResultStatus.NOT_FOUND );
        }
        if (!errorHandler.isOk()) {
            if (HttpStatus.NOT_FOUND.equals( errorHandler.getStatus() )) {
                log.warn( "execute(): Can't get data from youtrack, request failed with error NOT_FOUND. message: {}", response.getBody() );
                return errorSt( En_ResultStatus.NOT_FOUND );
            }
            log.warn( "execute(): Can't execute youtrack request, request failed with status {}. message: {} ", errorHandler.getStatus(), response.getBody() );
            return errorSt( En_ResultStatus.GET_DATA_ERROR );
        }

        return ok( response );
    }

    private RestTemplate makeClient( RestTemplateResponseErrorHandler errorHandler ) {
        RestTemplate template = new RestTemplate();
        if (errorHandler != null) {
            template.setErrorHandler( errorHandler );
        }
        ((DefaultUriTemplateHandler) template.getUriTemplateHandler()).setStrictEncoding( true );
        return template;
    }

    @Autowired
    private PortalConfig portalConfig;

    public void setAuthHeaders( HttpHeaders authHeaders ) {
        this.authHeaders = authHeaders;
    }

    private HttpHeaders authHeaders;

    private final static Logger log = LoggerFactory.getLogger( YoutrackRestDaoImpl.class );
}

class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    public boolean isOk() {
        return errorStatus == null || HttpStatus.OK.equals( errorStatus );
    }

    public HttpStatus getStatus() {
        return errorStatus;
    }

    @Override
    public boolean hasError( ClientHttpResponse httpResponse ) throws IOException {
        return (CLIENT_ERROR == httpResponse.getStatusCode().series()
                || SERVER_ERROR == httpResponse.getStatusCode().series());
    }

    @Override
    public void handleError( ClientHttpResponse httpResponse ) throws IOException {
        errorStatus = httpResponse.getStatusCode();
        log.warn( "handleError(): Youtrack http api request error. status code: {} : {}"
                , httpResponse.getStatusCode()
                , httpResponse.getStatusText()
        );
    }

    private HttpStatus errorStatus;
    private final static Logger log = LoggerFactory.getLogger( RestTemplateResponseErrorHandler.class );
}
