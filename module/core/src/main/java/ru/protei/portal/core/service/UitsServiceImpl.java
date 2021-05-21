package ru.protei.portal.core.service;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.client.uits.UitsDealStageMapping;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UitsIssueInfo;
import ru.protei.portal.core.model.helper.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Result<UitsIssueInfo> getIssueInfo(Long issueId) {
        if (issueId == null) {
            log.warn("getUitsIssueInfo(): Can't get UITS issue info. Argument issueId is mandatory");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        String resultStr;

        try {
            resultStr = getUitsCrmDeal(issueId);
        } catch (IOException e) {
            return Result.error(En_ResultStatus.GET_DATA_ERROR);
        }

        if (StringUtils.isEmpty(resultStr)){
            return Result.error(En_ResultStatus.GET_DATA_ERROR);
        }

        UitsIssueInfo uitsIssueInfo;
        try {
            uitsIssueInfo = convertToUitsIssueInfo(resultStr);
        } catch (JSONException e) {
            return Result.error(En_ResultStatus.GET_DATA_ERROR);
        }

        if (uitsIssueInfo == null){
            return Result.error(En_ResultStatus.GET_DATA_ERROR);
        }

        return Result.ok(uitsIssueInfo);
    }

    private UitsIssueInfo convertToUitsIssueInfo(String dealJson) throws JSONException {

        JSONObject json;
        try {
            json = new JSONObject(dealJson);
        } catch (JSONException e) {
            log.error("Failed to parse UitsCrmDeal result json", e);
            throw e;
        }

        JSONObject result = json.getJSONObject(RESULT);

        if (result == null){
            log.error("Failed to parse UitsCrmDeal json: result is null");
            return null;
        }

        UitsIssueInfo uitsIssueInfo = new UitsIssueInfo();
        String id = result.getString(ID);
        String title = result.getString(TITLE);
        String additionalInfo = result.getString(ADDITIONAL_INFO);
        String stageId = result.getString(STAGE_ID);
        Long stateId = UitsDealStageMapping.toCaseState(stageId);

        uitsIssueInfo.setId(id);
        uitsIssueInfo.setSummary(title);
        uitsIssueInfo.setDescription(additionalInfo);
        uitsIssueInfo.setState(stateId == null ? null : caseStateDAO.get(stateId));

        return uitsIssueInfo;
    }

    private String getUitsCrmDeal(Long issueId) throws IOException {
        String result = "";
        log.info("getUitsCrmDeal by issue id: {}", issueId);

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://support.uits.spb.ru/rest/347/djueym3pkassk34f/crm.deal.get?");

        List<NameValuePair> params = new ArrayList<>(1);
        params.add(new BasicNameValuePair("id", String.valueOf(issueId)));
        httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        HttpResponse response;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            log.error("Failed to execute getUitsCrmDeal request", e);
            throw e;
        }

        if (response == null){
            log.error("Failed to get UitsCrmDeal, HTTP response is null");
            return null;
        }

        InputStream inputStream;
        try {
            inputStream = response.getEntity().getContent();
        } catch (IOException e) {
            log.error("Failed to get UitsCrmDeal content", e);
            throw e;
        }

        if (inputStream == null){
            log.error("Failed to get UitsCrmDeal, content is null");
            return null;
        }

        try {
            result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to get UitsCrmDeal string from InputStream", e);
            throw e;
        }

        return result;
    }
}
