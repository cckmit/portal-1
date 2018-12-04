package ru.protei.portal.core.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.springframework.web.util.UriComponentsBuilder;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.yt.AttachmentResponse;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;
import ru.protei.portal.util.UriUtils;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by admin on 15/11/2017.
 */
public class YoutrackServiceImpl implements YoutrackService {
    private RestTemplate ytClient = new RestTemplate();

    private HttpHeaders authHeaders;
    private String BASE_URL;

    @Autowired
    private PortalConfig portalConfig;

    @PostConstruct
    public void initAuthHeadersAndUrl() {
        authHeaders = new HttpHeaders();
        authHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        authHeaders.setContentType( MediaType.APPLICATION_JSON_UTF8 );
        authHeaders.set("Authorization", "Bearer " + portalConfig.data().youtrack().getAuthToken());

        BASE_URL = portalConfig.data().youtrack().getApiBaseUrl();
    }

    @Override
    public ChangeResponse getIssueChanges(String issue) {
        log.debug("requesting changes of {}", issue);

        ResponseEntity<ChangeResponse> resp = ytClient.exchange(
                BASE_URL + "/rest/issue/" + issue + "/changes",
                HttpMethod.GET, new HttpEntity<>(authHeaders), ChangeResponse.class
        );
        return resp.getBody();
    }

    @Override
    public List<YtAttachment> getIssueAttachments(String issueId) {
        log.debug("requesting attachments of {}", issueId);

        ResponseEntity<AttachmentResponse> resp = ytClient.exchange(
                BASE_URL + "/rest/issue/" + issueId + "/attachment",
                HttpMethod.GET, new HttpEntity<>(authHeaders), AttachmentResponse.class
        );
        return resp.getBody().getAttachments();
    }

    @Override
    public String createIssue(String project, String summary, String description) {
        log.info("createIssue: project={}, summary={}, description={}", project, summary, description);

        String uri = UriComponentsBuilder.fromHttpUrl( "https://youtrack.protei.ru" + "/api/issues" )//TODO base_url
                .queryParam( "fields", "idReadable,id,name")
                .build()
                .toUriString();

        JSONObject projectJson = new JSONObject();
        projectJson.put( "id", project );

        JSONObject json = new JSONObject();
        json.put("project", projectJson );
        json.put("summary", summary);
        json.put("description", StringUtils.emptyIfNull(description));

//        JSONArray array = new JSONArray();//TODO telephone
//        array.put( new JSONObject(customFieldJsonString) );
//        json.put( "fields", array);

        ResponseEntity<String> response = ytClient.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(json.toString(), authHeaders),
                String.class
        );
        JSONObject result = new JSONObject( response.getBody() );
        String issueId = result.getString( "idReadable" );
        if (issueId == null) {
            log.error("createIssue: failed to create YT issue: failed to extract issue id from YT response body: {}", response.getBody());
            throw new RuntimeException();
        }

        log.debug("createIssue: with idReadable = {}", issueId);
        return issueId;
    }
//
//    public static void main(String [] args  ) {
//        makeCustomFielsPnone();
//    }
//    private static void makeCustomFielsPnone() {
//
//        JSONObject customField = new JSONObject(customFieldJsonString);
//
//int stop = 0;
//    }

    @Override
    public Set<String> getIssueIdsByProjectAndUpdatedAfter(String projectId, Date updatedAfter) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/rest/issue/byproject/" + projectId)
                .queryParam("with", "id")
                .queryParam("max", MAX_ISSUES_IN_RESPONSE);

        if (updatedAfter != null)
            builder.queryParam("updatedAfter", updatedAfter.getTime());

        String uri = builder.build()
                .encode()
                .toUriString();

        ResponseEntity<Issue[]> response = ytClient.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(authHeaders), Issue[].class
        );
        return Arrays.stream(response.getBody())
                .map(Issue::getId)
                .collect(Collectors.toSet());
    }

    private static String customFieldJsonString = "        {\n" +
            "            \"projectCustomField\": {\n" +
            "                \"bundle\": {\n" +
            "                    \"id\": \"35-87\",\n" +
            "                    \"$type\": \"jetbrains.charisma.customfields.complex.enumeration.EnumBundle\",\n" +
            "                    \"name\": \"AdminCRM: Тип заявки\",\n" +
            "                    \"isUpdateable\": false\n" +
            "                },\n" +
            "                \"ordinal\": 4,\n" +
            "                \"isPublic\": false,\n" +
            "                \"canBeEmpty\": true,\n" +
            "                \"emptyFieldText\": \"Прочее\",\n" +
            "                \"field\": {\n" +
            "                    \"ordinal\": 147,\n" +
            "                    \"fieldType\": {\n" +
            "                        \"valueType\": \"enum\",\n" +
            "                        \"isMultiValue\": true,\n" +
            "                        \"$type\": \"jetbrains.charisma.customfields.rest.FieldType\"\n" +
            "                    },\n" +
            "                    \"localizedName\": null,\n" +
            "                    \"name\": \"Тип заявки\",\n" +
            "                    \"id\": \"34-190\",\n" +
            "                    \"$type\": \"jetbrains.charisma.customfields.rest.CustomField\"\n" +
            "                },\n" +
            "                \"id\": \"67-1851\",\n" +
            "                \"$type\": \"jetbrains.charisma.customfields.complex.enumeration.EnumProjectCustomField\"\n" +
            "            },\n" +
            "            \"value\": [\n" +
            "                {\n" +
            "                    \"localizedName\": null,\n" +
            "                    \"description\": \"Задачи связанные с офисной телефонией\",\n" +
            "                    \"ordinal\": 7,\n" +
            "                    \"hasRunningJob\": false,\n" +
            "                    \"color\": {\n" +
            "                        \"id\": \"13\",\n" +
            "                        \"$type\": \"jetbrains.charisma.persistence.customfields.FieldStyle\"\n" +
            "                    },\n" +
            "                    \"name\": \"Телефония\",\n" +
            "                    \"id\": \"36-13132\",\n" +
            "                    \"$type\": \"jetbrains.charisma.customfields.complex.enumeration.EnumBundleElement\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"id\": \"67-1851\",\n" +
            "            \"$type\": \"jetbrains.charisma.customfields.complex.enumeration.MultiEnumIssueCustomField\"\n" +
            "        }"
            ;

    private final static Logger log = LoggerFactory.getLogger(YoutrackServiceImpl.class);
    private final static int MAX_ISSUES_IN_RESPONSE = Integer.MAX_VALUE;
}
