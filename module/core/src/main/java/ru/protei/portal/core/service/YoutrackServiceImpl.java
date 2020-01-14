package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.YoutrackConstansMapping;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.client.youtrack.http.YoutrackRequest;
import ru.protei.portal.core.client.youtrack.http.YoutrackUrlProvider;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.youtrack.YtFieldDescriptor;
import ru.protei.portal.core.model.youtrack.YtFieldNames;
import ru.protei.portal.core.model.youtrack.dto.activity.YtActivityCategory;
import ru.protei.portal.core.model.youtrack.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.youtrack.dto.activity.customfield.YtCustomFieldActivityItem;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtStateBundleElement;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtSimpleIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueAttachment;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueComment;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public class YoutrackServiceImpl implements YoutrackService {

    @Override
    public Result<List<YouTrackIssueStateChange>> getIssueStateChanges(String issueId) {
        return client.read(new YoutrackRequest<>(YtActivityItem[].class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issueActivities(issueId))
                .fillResponseWithPojo()
                .params(new HashMap<String, String>() {{
                    put("categories", YtActivityCategory.CustomFieldCategory.getCategoryId());
                }}))
                .map(Arrays::asList)
                .map(ytActivityItems -> {
                    ytActivityItems.sort(Comparator.comparing(
                            ytActivityItem -> ytActivityItem.timestamp,
                            Comparator.nullsFirst(Long::compareTo)
                    ));
                    return ytActivityItems;
                })
                .map(ytActivityItems -> CollectionUtils.stream(ytActivityItems)
                        .filter(ytActivityItem -> ytActivityItem.field != null)
                        .filter(ytActivityItem -> issueStateFieldNames().contains(ytActivityItem.field.name))
                        .filter(ytActivityItem -> ytActivityItem instanceof YtCustomFieldActivityItem)
                        .map(ytActivityItem -> (YtCustomFieldActivityItem) ytActivityItem)
                        .filter(ytCustomFieldActivityItem ->
                                CollectionUtils.isEmpty(ytCustomFieldActivityItem.added) ||
                                ytCustomFieldActivityItem.added.get(0) instanceof YtStateBundleElement
                        )
                        .map(this::convertYtCustomFieldActivityItem)
                        .collect(Collectors.toList()));
    }

    @Override
    public Result<String> createIssue(String projectName, String summary, String description) {
        log.info("createIssue(): projectName={}, summary={}, description={}", projectName, summary, description);

        Result<String> projectResult = client.read(new YoutrackRequest<>(YtProject[].class)
                .url(new YoutrackUrlProvider(getBaseUrl()).projects()))
                .map(Arrays::asList)
                .flatMap(projects -> {
                    if (projects.size() == 1) return ok(projects.get(0));
                    return error(En_ResultStatus.INCORRECT_PARAMS, "Found more/less than one project: " + projects.size());
                })
                .map(project -> project.id);
        if (projectResult.isError()) {
            log.info("createIssue(): projectName={}, summary={}, description={} | failed to get project", projectName, summary, description);
            return error(projectResult.getStatus(), projectResult.getMessage());
        }
        String projectId = projectResult.getData();

        return client.create(new YoutrackRequest<>(YtIssue.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issues())
                .save(makeNewBasicIssue(projectId, summary, description)))
                .map(ytIssue -> ytIssue.idReadable);
    }

    @Override
    public Result<Set<String>> getIssueIdsByProjectAndUpdatedAfter(String projectName, Date updatedAfter) {
        return client.read(new YoutrackRequest<>(YtIssue[].class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issues())
                .query(String.format("project: %s updated: %s .. *", projectName, dateToYtString(updatedAfter))))
                .map(issues -> Arrays.stream(issues)
                        .map(issue -> issue.idReadable)
                        .collect(Collectors.toSet()));
    }

    @Override
    public Result<YouTrackIssueInfo> getIssueInfo( String issueId ) {
        if (issueId == null) {
            log.warn("getYoutrackIssueInfo(): Can't get issue info. Argument issueId is mandatory");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return client.read(new YoutrackRequest<>(YtIssue.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issue(issueId))
                .fillResponseWithPojo()
                .fillResponseWithYt(YtIssueComment.class, YtIssueAttachment.class, YtUser.class, YtIssueCustomField.class))
                .map(this::convertYtIssue);
    }

    @Override
    public Result<YouTrackIssueInfo> setIssueCrmNumberIfDifferent(String issueId, Long caseNumber) {
        if (issueId == null || caseNumber == null) {
            log.warn("setIssueCrmNumber(): Can't set youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", issueId, caseNumber);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return client.read(new YoutrackRequest<>(YtIssue.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issue(issueId))
                .fillResponseWithPojo()
                .fillResponseWithYt(YtIssueCustomField.class, YtIssueComment.class, YtIssueAttachment.class))
                .flatMap(issue -> {
                    YtIssueCustomField field = issue.getField(YtFieldNames.crmNumber);
                    Long crmNumber = field == null ? null : NumberUtils.parseLong(field.getValue());
                    if (Objects.equals(crmNumber, caseNumber)) {
                        return ok(issue);
                    }
                    return setCrmNumber(issue.idReadable, caseNumber);
                })
                .map(this::convertYtIssue);
    }

    @Override
    public Result<YouTrackIssueInfo> removeIssueCrmNumberIfSame(String issueId, Long caseNumber) {
        if (issueId == null || caseNumber == null) {
            log.warn("removeIssueCrmNumber(): Can't remove youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", issueId, caseNumber);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return client.read(new YoutrackRequest<>(YtIssue.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issue(issueId))
                .fillResponseWithPojo()
                .fillResponseWithYt(YtIssueCustomField.class, YtIssueComment.class, YtIssueAttachment.class))
                .flatMap(issue -> {
                    YtIssueCustomField field = issue.getField(YtFieldNames.crmNumber);
                    Long crmNumber = field == null ? null : NumberUtils.parseLong(field.getValue());
                    if (Objects.equals(crmNumber, caseNumber)) {
                        return removeCrmNumber(issue.idReadable);
                    }
                    return ok(issue);
                })
                .map(this::convertYtIssue);
    }

    @Async(BACKGROUND_TASKS)
    @Override
    public void mergeYouTrackLinks( Long caseNumber, List<String> added, List<String> removed ) {

        for (String youtrackId : emptyIfNull( removed )) {
            removeIssueCrmNumberIfSame( youtrackId, caseNumber);
        }

        for (String youtrackId : emptyIfNull( added)) {
            setIssueCrmNumberIfDifferent( youtrackId, caseNumber );
        }
    }

    private Result<YtIssue> setCrmNumber(String issueId, Long caseNumber) {
        log.info("setCrmNumber(): issueId={}, caseNumber={}", issueId, caseNumber);
        YtIssue issue = new YtIssue();
        issue.customFields = new ArrayList<>();
        issue.customFields.add(makeCrmNumberCustomField(caseNumber));
        return client.update(new YoutrackRequest<>(YtIssue.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issue(issueId))
                .fillResponseWithPojo()
                .fillResponseWithYt(YtIssueCustomField.class, YtIssueComment.class, YtIssueAttachment.class)
                .save(issue));
    }

    private Result<YtIssue> removeCrmNumber(String issueId) {
        log.info("removeCrmNumber(): issueId={}", issueId);
        YtIssue issue = new YtIssue();
        issue.customFields = new ArrayList<>();
        issue.customFields.add(makeCrmNumberCustomField(null));
        return client.remove(new YoutrackRequest<>(YtIssue.class)
                .url(new YoutrackUrlProvider(getBaseUrl()).issue(issueId))
                .fillResponseWithPojo()
                .fillResponseWithYt(YtIssueCustomField.class, YtIssueComment.class, YtIssueAttachment.class)
                .remove(issue, new YtFieldDescriptor(YtSimpleIssueCustomField.class, "value")));
    }

    private YouTrackIssueInfo convertYtIssue(YtIssue issue) {
        if (issue == null) return null;
        YouTrackIssueInfo issueInfo = new YouTrackIssueInfo();
        issueInfo.setId(issue.idReadable);
        issueInfo.setSummary(issue.summary);
        issueInfo.setDescription(issue.description);
        issueInfo.setState(YoutrackConstansMapping.toCaseState(getIssueState(issue)));
        issueInfo.setImportance(YoutrackConstansMapping.toCaseImportance(getIssuePriority(issue)));
        issueInfo.setComments(CollectionUtils.stream(issue.comments)
                .map(this::convertYtIssueComment)
                .collect(Collectors.toList())
        );
        issueInfo.setAttachments(CollectionUtils.stream(issue.attachments)
                .map(this::convertYtIssueAttachment)
                .collect(Collectors.toList())
        );
        return issueInfo;
    }

    private CaseComment convertYtIssueComment(YtIssueComment issueComment) {
        CaseComment caseComment = new CaseComment();
        caseComment.setAuthorId(config.data().youtrack().getYoutrackUserId());
        caseComment.setCreated(issueComment.created == null ? null : new Date(issueComment.created));
        caseComment.setUpdated(issueComment.updated == null ? null : new Date(issueComment.updated));
        caseComment.setRemoteId(issueComment.id);
        caseComment.setOriginalAuthorName(issueComment.author != null ? issueComment.author.fullName : null);
        caseComment.setOriginalAuthorFullName(issueComment.author != null ? issueComment.author.fullName : null);
        caseComment.setText(issueComment.text);
        caseComment.setDeleted(issueComment.deleted);
        return caseComment;
    }

    private Pair<Attachment, CaseAttachment> convertYtIssueAttachment(YtIssueAttachment issueAttachment) {
        Attachment attachment = new Attachment();
        attachment.setCreated(issueAttachment.created == null ? null : new Date(issueAttachment.created));
        attachment.setCreatorId(config.data().youtrack().getYoutrackUserId());
        attachment.setFileName(issueAttachment.name);
        attachment.setExtLink(issueAttachment.url);
        attachment.setMimeType(issueAttachment.mimeType);
        CaseAttachment caseAttachment = new CaseAttachment();
        caseAttachment.setRemoteId(issueAttachment.id);
        return Pair.of(attachment, caseAttachment);
    }

    private YouTrackIssueStateChange convertYtCustomFieldActivityItem(YtCustomFieldActivityItem activityItem) {
        YtStateBundleElement added = CollectionUtils.isEmpty(activityItem.added)
                ? null
                : (YtStateBundleElement) activityItem.added.get(0);
        YtStateBundleElement removed = CollectionUtils.isEmpty(activityItem.removed)
                ? null
                : (YtStateBundleElement) activityItem.removed.get(0);
        YouTrackIssueStateChange issueStateChange = new YouTrackIssueStateChange();
        issueStateChange.setAdded(YoutrackConstansMapping.toCaseState(added != null ? added.name : null));
        issueStateChange.setRemoved(YoutrackConstansMapping.toCaseState(removed != null ? removed.name : null));
        issueStateChange.setTimestamp(activityItem.timestamp);
        issueStateChange.setAuthorLogin(activityItem.author != null ? activityItem.author.login : null);
        issueStateChange.setAuthorFullName(activityItem.author != null ? activityItem.author.fullName : null);
        return issueStateChange;
    }

    private YtIssue makeNewBasicIssue(String projectId /* id, not name! */, String summary, String description) {
        YtProject project = new YtProject();
        project.id = projectId;
        YtIssue issue = new YtIssue();
        issue.project = project;
        issue.summary = summary;
        issue.description = description;
        return issue;
    }

    private YtIssueCustomField makeCrmNumberCustomField(Long caseNumber) {
        YtSimpleIssueCustomField cf = new YtSimpleIssueCustomField();
        cf.name = YtFieldNames.crmNumber;
        cf.value = caseNumber == null ? null : String.valueOf(caseNumber);
        return cf;
    }

    private String dateToYtString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(date);
    }

    private String getBaseUrl() {
        return config.data().youtrack().getApiBaseUrl() + "/api";
    }

    private String getIssuePriority(YtIssue issue) {
        YtIssueCustomField field = issue.getField(YtFieldNames.priority);
        if (field == null) {
            return null;
        }
        return field.getValue();
    }

    private String getIssueState(YtIssue issue) {
        YtIssueCustomField field = getIssueStateField(issue);
        if (field == null) {
            return null;
        }
        return field.getValue();
    }

    private YtIssueCustomField getIssueStateField(YtIssue issue) {
        for (String stateFieldName : issueStateFieldNames()) {
            YtIssueCustomField issueCustomField = issue.getField(stateFieldName);
            if (issueCustomField != null) {
                return issueCustomField;
            }
        }
        return null;
    }

    private List<String> issueStateFieldNames() {
        return Arrays.asList(
                YtFieldNames.stateEng,
                YtFieldNames.stateRus,
                YtFieldNames.equipmentStateRus,
                YtFieldNames.acrmStateRus
        );
    }

    @Autowired
    YoutrackHttpClient client;
    @Autowired
    PortalConfig config;

    private final static Logger log = LoggerFactory.getLogger( YoutrackServiceImpl.class );
}
