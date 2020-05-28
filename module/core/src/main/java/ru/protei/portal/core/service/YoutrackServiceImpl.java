package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.YoutrackConstansMapping;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.youtrack.YtFieldDescriptor;
import ru.protei.portal.core.model.youtrack.dto.activity.customfield.YtCustomFieldActivityItem;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtEnumBundleElement;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtStateBundleElement;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.*;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueAttachment;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueComment;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;
import ru.protei.portal.core.model.youtrack.dto.value.YtTextFieldValue;

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
        return api.getIssueCustomFieldActivityItems(issueId)
                .map(ytActivityItems -> {
                    ytActivityItems.sort(Comparator.comparing(
                            ytActivityItem -> ytActivityItem.timestamp,
                            Comparator.nullsFirst(Long::compareTo)
                    ));
                    return ytActivityItems;
                })
                .map(ytActivityItems -> CollectionUtils.stream(ytActivityItems)
                        .filter(ytActivityItem -> ytActivityItem.field != null)
                        .filter(ytActivityItem -> Arrays.asList(YtIssue.getStateFieldNames()).contains(ytActivityItem.field.name))
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

        Result<String> projectResult = getProjectIdByName(projectName);

        YtIssue issue = makeNewBasicIssue(projectResult.getData(), summary, description);
        return api.createIssueAndReturnId(issue)
                .map(ytIssue -> ytIssue.idReadable);
    }

    @Override
    public Result<String> createFireWorkerIssue(String summary, String description) {
        log.info("createFireWorkerIssue(): summary={}, description={}", summary, description);

        Result<String> projectResult = getProjectIdByName(config.data().youtrack().getAdminProject());

        YtIssue issue = makeNewBasicIssue(projectResult.getData(), summary, description);
        YtIssueCustomField requestType = makeRequestTypeCustomField();
        issue.customFields = new ArrayList<>();
        issue.customFields.add(requestType);
        return api.createIssueAndReturnId(issue)
                .map(ytIssue -> ytIssue.idReadable);
    }

    @Override
    public Result<String> createCompany(String companyName) {
        log.info("createCompany(): companyName={}", companyName);

        YtEnumBundleElement company = makeBundleElement(companyName, null);
        return api.createCompany(company)
                .map(enumBundleElement -> enumBundleElement.id);
    }

    @Override
    public Result<String> updateCompanyName(String companyId, String companyName) {
        log.info("updateCompanyName(): companyId={}, companyName={}", companyId, companyName);

        YtEnumBundleElement companyToUpdate = makeBundleElement(companyName, null);
        return api.updateCompany(companyId, companyToUpdate)
                .map(enumBundleElement -> enumBundleElement.id);
    }

    @Override
    public Result<String> updateCompanyArchived(String companyId, Boolean archived) {
        log.info("updateCompanyArchived(): companyId={}, archived={}", companyId, archived);

        YtEnumBundleElement companyToUpdate = makeBundleElement(null, archived);
        return api.updateCompany(companyId, companyToUpdate)
                .map(enumBundleElement -> enumBundleElement.id);
    }

    @Override
    public Result<String> getCompanyByName(String companyName) {
        log.info("getCompanyByName(): companyName={}", companyName);

        Result<String> companyResult = api.getCompanyByName(companyName)
                .flatMap(companies -> {
                    if (companies.size() == 1)
                        if (companyName.equals(companies.get(0).name)) return ok(companies.get(0));
                    return error(En_ResultStatus.INCORRECT_PARAMS, "Found more/less than one company: " + companies.size());
                })
                .map(company -> company.id);
        if (companyResult.isError()) {
            log.info("getCompanyByName(): companyName={} | failed to get company", companyName);
            return error(companyResult.getStatus(), companyResult.getMessage());
        }

        return companyResult;
    }

    @Override
    public Result<Set<String>> getIssueIdsByProjectAndUpdatedAfter(String projectName, Date updatedAfter) {
        String query = String.format("project: %s updated: %s .. *", projectName, dateToYtString(updatedAfter));
        return api.getIssueIdsByQuery(query).map(issues -> CollectionUtils.stream(issues)
                .map(issue -> issue.idReadable)
                .collect(Collectors.toSet()));
    }

    @Override
    public Result<YouTrackIssueInfo> getIssueInfo(String issueId) {
        if (issueId == null) {
            log.warn("getYoutrackIssueInfo(): Can't get issue info. Argument issueId is mandatory");
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return api.getIssueWithFieldsCommentsAttachments(issueId)
                .map(this::convertYtIssue);
    }

    @Override
    public Result<YouTrackIssueInfo> addIssueCrmNumber(String issueId, Long caseNumber) {
        if (issueId == null || caseNumber == null) {
            log.warn("addIssueCrmNumber(): Can't set youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", issueId, caseNumber);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return api.getIssueWithFieldsCommentsAttachments(issueId)
                .flatMap(issue -> {
                    YtTextIssueCustomField field = (YtTextIssueCustomField) issue.getCrmNumberField();
                    String crmNumbers = field == null || field.getValue() == null ? null : field.getValue().text;
                    return addCrmNumbers(issue.idReadable, caseNumber, crmNumbers);
                });
    }

    @Override
    public Result<YouTrackIssueInfo> removeIssueCrmNumber(String issueId, Long caseNumber) {
        if (issueId == null || caseNumber == null) {
            log.warn("removeIssueCrmNumber(): Can't remove youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", issueId, caseNumber);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return api.getIssueWithFieldsCommentsAttachments(issueId)
                .flatMap(issue -> {
                    YtTextIssueCustomField field = (YtTextIssueCustomField) issue.getCrmNumberField();
                    String crmNumbers = field == null || field.getValue() == null ? null : field.getValue().text;
                    return removeCrmNumber(issue.idReadable, caseNumber, crmNumbers);
                });
    }

    @Override
    public Result<YouTrackIssueInfo> addIssueSystemComment(String issueId, String text) {
        if (issueId == null || text == null) {
            log.warn("addIssueSystemComment(): Can't add system comment. All arguments are mandatory issueId={} text={}", issueId, text);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        YtUser commentAuthor = new YtUser();
        commentAuthor.login = config.data().youtrack().getLogin();

        YtIssueComment comment = new YtIssueComment();
        comment.author = commentAuthor;
        comment.text = text;

        YtIssue issue = new YtIssue();
        issue.comments = new ArrayList<>();
        issue.comments.add(comment);

        return api.updateIssueAndReturnWithFieldsCommentsAttachments(issueId, issue)
                .map(this::convertYtIssue);
    }

    @Async(BACKGROUND_TASKS)
    @Override
    public void mergeYouTrackLinks( Long caseNumber, List<String> added, List<String> removed ) {

        for (String youtrackId : emptyIfNull( removed )) {
            removeIssueCrmNumber( youtrackId, caseNumber);
        }

        for (String youtrackId : emptyIfNull( added)) {
            addIssueCrmNumber( youtrackId, caseNumber );
        }
    }

    private Result<String> getProjectIdByName (String projectName){
        Result<String> projectResult = api.getProjectIdByName(projectName)
                .flatMap(projects -> {
                    if (projects.size() == 1) return ok(projects.get(0));
                    return error(En_ResultStatus.INCORRECT_PARAMS, "Found more/less than one project: " + projects.size());
                })
                .map(project -> project.id);
        if (projectResult.isError()) {
            log.info("getProjectIdByName(): projectName={} | failed to get project", projectName);
            return error(projectResult.getStatus(), projectResult.getMessage());
        }

        return projectResult;
    }

    private Result<YouTrackIssueInfo> addCrmNumbers(String issueId, Long caseNumber, String crmNumbers) {
        log.info("setCrmNumbers(): issueId={}, caseNumber={}, crmNumbers={}", issueId, caseNumber, crmNumbers);
        YtIssue issue = new YtIssue();
        issue.customFields = new ArrayList<>();
        issue.customFields.add(addCaseNumberToCrmNumbers(caseNumber, crmNumbers));
        return api.updateIssueAndReturnWithFieldsCommentsAttachments(issueId, issue)
                .map(this::convertYtIssue);
    }

    private Result<YouTrackIssueInfo> removeCrmNumber(String issueId, Long caseNumber, String crmNumbers) {
        log.info("removeCrmNumber(): issueId={}, caseNumber={}, crmNumbers={}", issueId, caseNumber, crmNumbers);
        YtIssue issue = new YtIssue();
        issue.customFields = new ArrayList<>();
        issue.customFields.add(removeCaseNumberFromCrmNumbers(caseNumber, crmNumbers));
        YtFieldDescriptor crmNumberField = new YtFieldDescriptor(YtSimpleIssueCustomField.class, "value");
        return api.removeIssueFieldAndReturnWithFieldsCommentsAttachments(issueId, issue, crmNumberField)
                .map(this::convertYtIssue);
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

    private YtEnumBundleElement makeBundleElement(String elementName, Boolean isArchived) {
        YtEnumBundleElement element = new YtEnumBundleElement();
        element.name = elementName;
        element.archived = isArchived;
        return element;
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

    private YtIssueCustomField addCaseNumberToCrmNumbers(Long caseNumber, String crmNumbers) {
        YtTextIssueCustomField cf = new YtTextIssueCustomField();
        YtTextFieldValue textFieldValue = new YtTextFieldValue();
        cf.name = YtIssue.CustomFieldNames.crmNumbers;
        cf.value = textFieldValue;

        if (crmNumbers != null && crmNumbers.contains(caseNumber.toString())){
            textFieldValue.text = crmNumbers;
        } else {
            textFieldValue.text = crmNumbers == null || crmNumbers.isEmpty() ? "" : crmNumbers + "\n";
            textFieldValue.text += "[" + caseNumber + "]" + "(" + config.data().getCaseLinkConfig().getLinkCrm().replace("%id%", caseNumber.toString()) + ")";
        }

        return cf;
    }

    private YtIssueCustomField removeCaseNumberFromCrmNumbers(Long caseNumber, String crmNumbers) {
        YtTextIssueCustomField cf = new YtTextIssueCustomField();
        YtTextFieldValue textFieldValue = new YtTextFieldValue();
        cf.name = YtIssue.CustomFieldNames.crmNumbers;
        cf.value = textFieldValue;

        if (crmNumbers == null || crmNumbers.isEmpty() || !crmNumbers.contains(caseNumber.toString())) {
            textFieldValue.text = crmNumbers;
            return cf;
        }

        String[] lines = crmNumbers.split("\n");

        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            if (!line.contains(caseNumber.toString())){
                result.append(line).append("\n");
            }
        }

        if (result.toString().isEmpty()){
            return cf;
        }

        textFieldValue.text = result.toString().substring(0, result.toString().length() - 1);

        return cf;
    }

    private YtIssueCustomField makeRequestTypeCustomField(){
        YtSingleEnumIssueCustomField singleEnum = new YtSingleEnumIssueCustomField();
        singleEnum.name = YtIssue.CustomFieldNames.requestType;
        YtEnumBundleElement bundleElement = new YtEnumBundleElement();
        bundleElement.name = CrmConstants.Youtrack.REQUEST_TYPE_VALUE;
        singleEnum.value = bundleElement;
        return singleEnum;
    }

    private String dateToYtString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(date);
    }

    private String getIssuePriority(YtIssue issue) {
        YtSingleEnumIssueCustomField field = (YtSingleEnumIssueCustomField) issue.getPriorityField();
        if (field == null) {
            return null;
        }
        return field.getValueAsString();
    }

    private String getIssueState(YtIssue issue) {
        YtIssueCustomField field = issue.getStateField();
        if (field == null) {
            return null;
        }

        if (field instanceof YtStateIssueCustomField){
            return ((YtStateIssueCustomField) field).getValueAsString();
        }

        if (field instanceof YtStateMachineIssueCustomField){
            return ((YtStateMachineIssueCustomField) field).getValueAsString();
        }

        return null;
    }

    @Autowired
    YoutrackApi api;
    @Autowired
    PortalConfig config;

    private final static Logger log = LoggerFactory.getLogger( YoutrackServiceImpl.class );
}
