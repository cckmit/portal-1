package ru.protei.portal.core.client.youtrack.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.model.yt.api.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.yt.api.customfield.issue.YtSimpleIssueCustomField;
import ru.protei.portal.core.model.yt.api.issue.YtIssue;
import ru.protei.portal.core.model.yt.fields.YtFields;

import java.util.ArrayList;

public class YoutrackApiClientImpl implements YoutrackApiClient {

    @Override
    public Result<YtIssue> getIssue(String issueId) {
        log.info("getIssue(): issueId={}", issueId);
        String url = new YoutrackUrlProvider(getBaseUrl()).issue(issueId);
        return client.read(url, YtIssue.class);
    }

    @Override
    public Result<YtIssue> setCrmNumber(String issueId, Long caseNumber) {
        log.info("setCrmNumber(): issueId={}, caseNumber={}", issueId, caseNumber);
        YtIssue issue = new YtIssue();
        issue.customFields = new ArrayList<>();
        issue.customFields.add(makeCrmNumberCustomField(caseNumber));
        String url = new YoutrackUrlProvider(getBaseUrl()).issue(issueId);
        return client.save(url, YtIssue.class, issue);
    }

    @Override
    public Result<YtIssue> removeCrmNumber(String issueId) {
        log.info("removeCrmNumber(): issueId={}", issueId);
        YtIssue issue = new YtIssue();
        issue.customFields = new ArrayList<>();
        issue.customFields.add(makeCrmNumberCustomField(null));
        String url = new YoutrackUrlProvider(getBaseUrl()).issue(issueId);
        return client.save(url, YtIssue.class, issue, "value");
    }

    private YtIssueCustomField makeCrmNumberCustomField(Long caseNumber) {
        YtSimpleIssueCustomField cf = new YtSimpleIssueCustomField();
        cf.name = YtFields.crmNumber;
        cf.value = caseNumber == null ? null : String.valueOf(caseNumber);
        return cf;
    }

    private String getBaseUrl() {
        return portalConfig.data().youtrack().getApiBaseUrl() + "/api";
    }

    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private YoutrackHttpClient client;

    private final static Logger log = LoggerFactory.getLogger( YoutrackApiClientImpl.class );
}

