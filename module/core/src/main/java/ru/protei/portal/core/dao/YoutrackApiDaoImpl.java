package ru.protei.portal.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.AttachmentResponse;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;
import ru.protei.portal.core.model.yt.fields.YtFields;
import ru.protei.portal.core.service.YoutrackServiceImpl;
import ru.protei.portal.util.UriUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.CoreResponse.errorSt;
import static ru.protei.portal.api.struct.CoreResponse.ok;
import static ru.protei.portal.core.model.dict.En_ResultStatus.NOT_CREATED;

/**
 *
 */
public class YoutrackApiDaoImpl implements YoutrackApiDAO {

    @PostConstruct
    public void initAuthHeadersAndUrl() {
        BASE_URL = portalConfig.data().youtrack().getApiBaseUrl();
        BASE_URL = BASE_URL.replaceAll( "/rest", "/api" );
    }

    public static void main(String[] args) {
        YoutrackApiDaoImpl dao = new YoutrackApiDaoImpl();
        HttpHeaders  authHeaders = new HttpHeaders();
        authHeaders.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );
        authHeaders.setContentType( MediaType.APPLICATION_JSON_UTF8 );
        authHeaders.set( "Authorization", "Bearer perm:cG9ydGFs.cG9ydGFs.IOOlyzNfo22S7FpoanxYQB7Ap9FW7e");
        YoutrackHttpClientImpl client = new YoutrackHttpClientImpl();
        client.setAuthHeaders( authHeaders );
       String BASE_URL = "https://youtrack.protei.ru/api";

        String body =  "{ \"customFields\": [ {\"name\":\""+YtFields.crmNumber+"\",\"$type\":\"SimpleIssueCustomField\",\"value\":null} ] }";

        CoreResponse<String> response =
                client.update( BASE_URL + "/issues/PG-209", String.class, body );
        int stop = 0;
    }

    @Override
    public CoreResponse<String> removeCrmNumber( String issueId ) {

        String body =  "{ \"customFields\": [ {\"name\":\""+YtFields.crmNumber+"\",\"$type\":\"SimpleIssueCustomField\",\"value\":null} ] }";

        return client.update(  BASE_URL +"/issues/"+issueId, String.class, body );
    }

//    @Override
//    public CoreResponse<String> setCrmNumber( String issueId, Long caseNumber ) {
//        return client.update( makeYoutrackCommand( issueId, YtFields.crmNumber, String.valueOf( caseNumber ) ), String.class );
//    }
//
//    @Override
//    public CoreResponse<Issue> getIssue( String issueId ) {
//        return client.read( BASE_URL + "/issue/" + issueId, Issue.class );
//    }

    private String makeYoutrackCommand( String issueId, String fieldname, String fieldValue ) {
        return UriComponentsBuilder.fromHttpUrl( BASE_URL + "/issue/" + issueId + "/execute" )
                .queryParam( "command", fieldname + " " + fieldValue )
                .build()
                .encode()
                .toUriString();
    }



    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YoutrackHttpClient client;

    private String BASE_URL;

    private final static Logger log = LoggerFactory.getLogger( YoutrackApiDaoImpl.class );
}

