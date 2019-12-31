package ru.protei.portal.core.client.youtrack.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.yt.api.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.yt.api.customfield.issue.YtSimpleIssueCustomField;
import ru.protei.portal.core.model.yt.api.issue.YtIssue;
import ru.protei.portal.core.model.yt.api.issue.YtIssueAttachment;
import ru.protei.portal.core.model.yt.api.project.YtProject;
import ru.protei.portal.core.model.yt.fields.YtFields;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;

public class YoutrackApiClientImpl implements YoutrackApiClient {

    @Override
    public Result<YtIssue> createIssue(String projectName, String summary, String description) {
        log.info("createIssue(): projectName={}, summary={}, description={}", projectName, summary, description);

        Result<List<YtProject>> result = getProjectsByName(projectName);
        if (result.isError()) {
            log.info("createIssue(): projectName={}, summary={}, description={} | failed to get projects", projectName, summary, description);
            return error(result.getStatus(), result.getMessage());
        }

        List<YtProject> projects = result.getData();
        if (projects.size() != 1) {
            log.info("createIssue(): projectName={}, summary={}, description={} | unable to find project", projectName, summary, description);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        YtProject project = new YtProject();
        project.id = projects.get(0).id;

        YtIssue issue = new YtIssue();
        issue.project = project;
        issue.summary = summary;
        issue.description = description;

        String url = new YoutrackUrlProvider(getBaseUrl()).issues();
        return client.save(url, YtIssue.class, issue);
    }

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

    @Override
    public Result<List<YtIssueAttachment>> getIssueAttachments(String issueId) {
        log.info("getIssueAttachments(): issueId={}", issueId);
        String url = new YoutrackUrlProvider(getBaseUrl()).issueAttachments(issueId);
        return client.read(url, YtIssueAttachment[].class)
                .map(Arrays::asList);
    }

    @Override
    public Result<List<YtProject>> getProjectsByName(String projectName) {
        log.info("getProjectsByName(): projectName={}", projectName);
        String url = new YoutrackUrlProvider(getBaseUrl()).projects();
        return client.read(url, projectName, YtProject[].class)
                .map(Arrays::asList);
    }

    @Override
    public Result<List<YtIssue>> getIssuesByProjectAndUpdated(String projectName, Date updatedAfter) {
        log.info("getIssuesByProjectAndUpdated(): projectName={}, updatedAfter={}", projectName, updatedAfter);
        String url = new YoutrackUrlProvider(getBaseUrl()).issues();
        String query = String.format("project: %s updated: %s .. *", projectName, dateToYtString(updatedAfter));
        return client.read(url, query, YtIssue[].class)
                .map(Arrays::asList);
    }

    private YtIssueCustomField makeCrmNumberCustomField(Long caseNumber) {
        YtSimpleIssueCustomField cf = new YtSimpleIssueCustomField();
        cf.name = YtFields.crmNumber;
        cf.value = caseNumber == null ? null : String.valueOf(caseNumber);
        return cf;
    }

    private String dateToYtString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(date);
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

