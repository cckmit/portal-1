package ru.protei.portal.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.uits.UitsDealStageMapping;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.UitsIssueInfo;

import java.io.IOException;

import static ru.protei.portal.api.struct.Result.error;

public class UitsServiceImpl implements UitsService {

    private final static Logger log = LoggerFactory.getLogger( UitsServiceImpl.class );
    public static final String RESULT = "result";
    public static final String ADDITIONAL_INFO = "ADDITIONAL_INFO";
    public static final String TITLE = "TITLE";
    public static final String STAGE_ID = "STAGE_ID";
    public static final String ID = "ID";

    @Autowired
    CaseStateDAO caseStateDAO;
    @Autowired
    PortalConfig config;

    @Override
    public Result<UitsIssueInfo> getIssueInfo(Long issueId) {
        if (issueId == null) {
            log.warn("getIssueInfo(): Can't get UITS issue info. issueId is mandatory");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("id", String.valueOf(issueId));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, new HttpHeaders());
        RestTemplate restTemplate = new RestTemplate();

        String url = config.data().uits().getApiUrl() + "?";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(response.getBody());
        } catch (IOException e) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        return Result.ok(convertToUitsIssueInfo(root));
    }

    private UitsIssueInfo convertToUitsIssueInfo(JsonNode json) {

        JsonNode result = json.get(RESULT);

        if (result == null){
            log.error("Failed to parse UITS json: result field is null");
            return null;
        }

        String id = result.get(ID).asText();
        String summary = result.get(TITLE).asText();
        String description = result.get(ADDITIONAL_INFO).asText();
        String stageId = result.get(STAGE_ID).asText();
        Long caseStateId = UitsDealStageMapping.toCaseState(stageId);
        CaseState caseState = caseStateId == null ? null : caseStateDAO.get(caseStateId);

        return new UitsIssueInfo(id, summary, description, caseState);
    }
}
