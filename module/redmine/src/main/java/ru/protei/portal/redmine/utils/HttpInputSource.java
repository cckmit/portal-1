package ru.protei.portal.redmine.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class HttpInputSource implements InputStreamSource {

    public HttpInputSource(String url, String apiKey) {
        httpClient = HttpClients.createDefault();
        request = RequestBuilder.get()
                .setUri(url)
                .setHeader("x-redmine-api-key", apiKey)
                .build();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        final HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

    private final static Logger logger = LoggerFactory.getLogger(HttpInputSource.class);

    private final HttpClient httpClient;

    private final HttpUriRequest request;
}
