package ru.protei.portal.core.client.youtrack.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    public boolean isOk() {
        return erroratus == null || HttpStatus.OK.equals( erroratus );
    }

    public HttpStatus getStatus() {
        return erroratus;
    }

    @Override
    public boolean hasError( ClientHttpResponse httpResponse ) throws IOException {
        return (CLIENT_ERROR == httpResponse.getStatusCode().series()
                || SERVER_ERROR == httpResponse.getStatusCode().series());
    }

    @Override
    public void handleError( ClientHttpResponse httpResponse ) throws IOException {
        erroratus = httpResponse.getStatusCode();
        log.warn( "handleError(): Youtrack http api request error. status code: {} : {}"
                , httpResponse.getStatusCode()
                , httpResponse.getStatusText()
        );
    }

    private HttpStatus erroratus;
    private final static Logger log = LoggerFactory.getLogger( RestTemplateResponseErrorHandler.class );
}
