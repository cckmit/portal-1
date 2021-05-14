package ru.protei.portal.core.service;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UitsIssueInfo;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;

public class UitsServiceImpl implements UitsService {
    private final static Logger log = LoggerFactory.getLogger( UitsServiceImpl.class );

    @Autowired
    YoutrackApi api;
    @Autowired
    PortalConfig config;
    @Autowired
    CaseStateDAO caseStateDAO;

    @Override
    public Result<UitsIssueInfo> getIssueInfo(Long issueId) {
        if (issueId == null) {
            log.warn("getUitsIssueInfo(): Can't get issue info. Argument issueId is mandatory");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
//        return api.getIssueWithFieldsCommentsAttachments(issueId)
//                .map(this::convertYtIssue);

        //TODO remove stub
        UitsIssueInfo uitsIssueInfo = new UitsIssueInfo();
        uitsIssueInfo.setId(String.valueOf(issueId));
        uitsIssueInfo.setDescription("UITS Stub description");
        uitsIssueInfo.setSummary("UITS Stub summary");
        Result<UitsIssueInfo> result = new Result<UitsIssueInfo>().ok(uitsIssueInfo);

        String resultStr = doUitsRequest(issueId);

        return result;
    }

    private String doUitsRequest(Long issueId) {
        String result = "";

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://support.uits.spb.ru/rest/347/djueym3pkassk34f/crm.deal.get?id=59597");

//        List<NameValuePair> params = new ArrayList<>(1);
//        params.add(new BasicNameValuePair("id", String.valueOf(issueId)));
//        httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == null){
            log.warn("UITS crm.deal.get response is null");
            return "";
        }

        HttpEntity entity = response.getEntity();
        InputStream inputStream = null;
        try {
            inputStream = entity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputStream == null){
            log.warn("UITS crm.deal.get inputStream is null");
            return "";
        }

        try {
            result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

//    private UitsIssueInfo convertYtIssue(YtIssue issue) {
//        if (issue == null) return null;
//        YouTrackIssueInfo issueInfo = new YouTrackIssueInfo();
//        issueInfo.setId(issue.idReadable);
//        issueInfo.setSummary(issue.summary);
//        issueInfo.setDescription(issue.description);
//        Long stateId = YoutrackConstansMapping.toCaseState(getIssueState(issue));
//        issueInfo.setState(stateId == null ? null : caseStateDAO.get(stateId));
//        issueInfo.setComments(CollectionUtils.stream(issue.comments)
//                .map(this::convertYtIssueComment)
//                .map(Result::getData)
//                .collect(Collectors.toList())
//        );
//        issueInfo.setAttachments(CollectionUtils.stream(issue.attachments)
//                .map(this::convertYtIssueAttachment)
//                .collect(Collectors.toList())
//        );
//        return issueInfo;
//    }
//
//    private Pair<Attachment, CaseAttachment> convertYtIssueAttachment(YtIssueAttachment issueAttachment) {
//        Attachment attachment = new Attachment();
//        attachment.setCreated(issueAttachment.created == null ? null : new Date(issueAttachment.created));
//        attachment.setCreatorId(config.data().youtrack().getYoutrackUserId());
//        attachment.setFileName(issueAttachment.name);
//        attachment.setExtLink(issueAttachment.url);
//        attachment.setMimeType(issueAttachment.mimeType);
//        CaseAttachment caseAttachment = new CaseAttachment();
//        caseAttachment.setRemoteId(issueAttachment.id);
//        return Pair.of(attachment, caseAttachment);
//    }
//
//    private String getIssueState(YtIssue issue) {
//        YtIssueCustomField field = issue.getStateField();
//        if (field == null) {
//            return null;
//        }
//
//        if (field instanceof YtStateIssueCustomField){
//            return ((YtStateIssueCustomField) field).getValueAsString();
//        }
//
//        if (field instanceof YtStateMachineIssueCustomField){
//            return ((YtStateMachineIssueCustomField) field).getValueAsString();
//        }
//
//        return null;
//    }
}
