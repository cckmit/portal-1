package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.YoutrackConstansMapping;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiClient;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.yt.YtFieldNames;
import ru.protei.portal.core.model.yt.dto.activity.YtActivityCategory;
import ru.protei.portal.core.model.yt.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.yt.dto.activity.customfield.YtCustomFieldActivityItem;
import ru.protei.portal.core.model.yt.dto.bundleelemenet.YtStateBundleElement;
import ru.protei.portal.core.model.yt.dto.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.yt.dto.issue.YtIssue;
import ru.protei.winter.core.utils.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class YoutrackServiceImpl implements YoutrackService {

    @Override
    public Result<List<YouTrackIssueStateChange>> getIssueStateChanges(String issueId) {
        return getIssueCustomFieldsChanges(issueId)
                .map(ytActivityItems -> CollectionUtils.stream(ytActivityItems)
                        .filter(ytActivityItem -> ytActivityItem.field != null)
                        .filter(ytActivityItem -> issueStateFieldNames().contains(ytActivityItem.field.name))
                        .filter(ytActivityItem -> ytActivityItem instanceof YtCustomFieldActivityItem)
                        .map(ytActivityItem -> (YtCustomFieldActivityItem) ytActivityItem)
                        .filter(ytCustomFieldActivityItem -> CollectionUtils.isEmpty(ytCustomFieldActivityItem.added) || ytCustomFieldActivityItem.added.get(0) instanceof YtStateBundleElement)
                        .map(ytCustomFieldActivityItem -> {
                            YtStateBundleElement added = CollectionUtils.isEmpty(ytCustomFieldActivityItem.added)
                                    ? null
                                    : (YtStateBundleElement) ytCustomFieldActivityItem.added.get(0);
                            YtStateBundleElement removed = CollectionUtils.isEmpty(ytCustomFieldActivityItem.removed)
                                    ? null
                                    : (YtStateBundleElement) ytCustomFieldActivityItem.removed.get(0);
                            YouTrackIssueStateChange issueStateChange = new YouTrackIssueStateChange();
                            issueStateChange.setAdded(YoutrackConstansMapping.toCaseState(added != null ? added.name : null));
                            issueStateChange.setRemoved(YoutrackConstansMapping.toCaseState(removed != null ? removed.name : null));
                            issueStateChange.setTimestamp(ytCustomFieldActivityItem.timestamp);
                            issueStateChange.setAuthorLogin(ytCustomFieldActivityItem.author != null ? ytCustomFieldActivityItem.author.login : null);
                            issueStateChange.setAuthorFullName(ytCustomFieldActivityItem.author != null ? ytCustomFieldActivityItem.author.fullName : null);
                            return issueStateChange;
                        })
                        .collect(Collectors.toList())
                );
    }

    @Override
    public Result<String> createIssue(String projectName, String summary, String description) {
        return apiDao.createIssue( projectName, summary, description )
                .map(ytIssue -> ytIssue.idReadable);
    }

    @Override
    public Result<Set<String>> getIssueIdsByProjectAndUpdatedAfter(String projectName, Date updatedAfter) {
        return apiDao.getIssuesByProjectAndUpdated(projectName, updatedAfter)
                .map(issues -> stream(issues)
                        .map(issue -> issue.idReadable)
                        .collect(Collectors.toSet())
                );
    }

    @Override
    public Result<YouTrackIssueInfo> getIssueInfo( String issueId ) {
        if (issueId == null) {
            log.warn("getYoutrackIssueInfo(): Can't get issue info. Argument issueId is mandatory");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return apiDao.getIssue(issueId)
                .map(this::convertToInfo);
    }

    @Override
    public Result<YouTrackIssueInfo> setIssueCrmNumberIfDifferent(String ytIssueId, Long caseNumber) {
        if (ytIssueId == null || caseNumber == null) {
            log.warn("setIssueCrmNumber(): Can't set youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", ytIssueId, caseNumber);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return apiDao.getIssue(ytIssueId)
                .flatMap(issue -> {
                    YtIssueCustomField field = issue.getField(YtFieldNames.crmNumber);
                    Long crmNumber = field == null ? null : NumberUtils.parseLong(field.getValue());
                    return replaceCrmNumberIfDifferent(issue.idReadable, crmNumber, caseNumber);
                })
                .map(this::convertToInfo);
    }

    @Override
    public Result<YouTrackIssueInfo> removeIssueCrmNumberIfSame(String ytIssueId, Long caseNumber) {
        if (ytIssueId == null || caseNumber == null) {
            log.warn("removeIssueCrmNumber(): Can't remove youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", ytIssueId, caseNumber);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return apiDao.getIssue(ytIssueId)
                .flatMap(issue -> {
                    YtIssueCustomField field = issue.getField(YtFieldNames.crmNumber);
                    Long crmNumber = field == null ? null : NumberUtils.parseLong(field.getValue());
                    return removeCrmNumberIfSame(issue.idReadable, crmNumber, caseNumber);
                })
                .map(this::convertToInfo);
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

    private Result<List<YtActivityItem>> getIssueCustomFieldsChanges(String issueId) {
        return apiDao.getIssueActivityChanges(issueId, YtActivityCategory.CustomFieldCategory)
                .map(ytActivityItems -> {
                    ytActivityItems.sort(Comparator.comparing(
                            ytActivityItem -> ytActivityItem.timestamp,
                            Comparator.nullsFirst(Long::compareTo)
                    ));
                    return ytActivityItems;
                });
    }

    private Result<YtIssue> removeCrmNumberIfSame(String ytIssueId, Long crmNumber, Long caseNumber) {
        if (Objects.equals(crmNumber, caseNumber)) {
            return apiDao.removeCrmNumber(ytIssueId);
        }
        return ok();
    }

    private Result<YtIssue> replaceCrmNumberIfDifferent(String ytIssueId, Long crmNumber, Long caseNumber) {
        if (Objects.equals(crmNumber, caseNumber)) {
            return ok();
        }
        return apiDao.setCrmNumber(ytIssueId, caseNumber);
    }

    private YouTrackIssueInfo convertToInfo(YtIssue issue) {
        if (issue == null) return null;
        YouTrackIssueInfo issueInfo = new YouTrackIssueInfo();
        issueInfo.setId(issue.idReadable);
        issueInfo.setSummary(issue.summary);
        issueInfo.setDescription(issue.description);
        issueInfo.setState(YoutrackConstansMapping.toCaseState(getIssueState(issue)));
        issueInfo.setImportance(YoutrackConstansMapping.toCaseImportance(getIssuePriority(issue)));
        issueInfo.setComments(CollectionUtils.stream(issue.comments)
                .map(ytIssueComment -> {
                    CaseComment caseComment = new CaseComment();
                    caseComment.setAuthorId(config.data().youtrack().getYoutrackUserId());
                    caseComment.setCreated(new Date(ytIssueComment.created));
                    caseComment.setUpdated(new Date(ytIssueComment.updated));
                    caseComment.setRemoteId(ytIssueComment.id);
                    caseComment.setOriginalAuthorName(ytIssueComment.author != null ? ytIssueComment.author.fullName : null);
                    caseComment.setOriginalAuthorFullName(ytIssueComment.author != null ? ytIssueComment.author.fullName : null);
                    caseComment.setText(ytIssueComment.text);
                    caseComment.setDeleted(ytIssueComment.deleted);
                    return caseComment;
                })
                .collect(Collectors.toList())
        );
        issueInfo.setAttachments(CollectionUtils.stream(issue.attachments)
                .map(ytIssueAttachment -> {
                    Attachment attachment = new Attachment();
                    attachment.setCreated(new Date(ytIssueAttachment.created));
                    attachment.setCreatorId(config.data().youtrack().getYoutrackUserId());
                    attachment.setFileName(ytIssueAttachment.name);
                    attachment.setExtLink(ytIssueAttachment.url);
                    attachment.setMimeType(ytIssueAttachment.mimeType);
                    CaseAttachment caseAttachment = new CaseAttachment();
                    caseAttachment.setRemoteId(ytIssueAttachment.id);
                    return Pair.create(attachment, caseAttachment);
                })
                .collect(Collectors.toList())
        );
        return issueInfo;
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
    YoutrackApiClient apiDao;
    @Autowired
    PortalConfig config;

    private final static Logger log = LoggerFactory.getLogger( YoutrackServiceImpl.class );

}

