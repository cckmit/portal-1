package ru.protei.portal.jira.utils;

import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.internal.json.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static com.atlassian.jira.rest.client.api.domain.IssueFieldId.*;
import static com.atlassian.jira.rest.client.internal.json.JsonParseUtil.parseOptionalJsonObject;

/**
 * копипаста из com.atlassian.jira.rest.client.internal.json.IssueJsonParser
 * оригинал не работает, ожидая какие-то дополнительные поля в JSON-структуре,
 * и кроме того, большая часть методов private, так что..
 */
public class CustomJiraIssueParser implements JsonObjectParser<Issue> {
    private final BasicIssueJsonParser basicIssueJsonParser = new BasicIssueJsonParser();
    private final StatusJsonParser statusJsonParser = new StatusJsonParser();
    private final VersionJsonParser versionJsonParser = new VersionJsonParser();
    private final AttachmentJsonParser attachmentJsonParser = new AttachmentJsonParser();
    private final CommentJsonParser commentJsonParser = new CommentJsonParser();
    private final IssueTypeJsonParser issueTypeJsonParser = new IssueTypeJsonParser();
    private final BasicProjectJsonParser projectJsonParser = new BasicProjectJsonParser();
    private final BasicPriorityJsonParser priorityJsonParser = new BasicPriorityJsonParser();
    private final ResolutionJsonParser resolutionJsonParser = new ResolutionJsonParser();
    private final UserJsonParser userJsonParser = new UserJsonParser();
    private final SubtaskJsonParser subtaskJsonParser = new SubtaskJsonParser();
    private final ChangelogJsonParser changelogJsonParser = new ChangelogJsonParser();
    private final OperationsJsonParser operationsJsonParser = new OperationsJsonParser();
    private final JsonWeakParserForString jsonWeakParserForString = new JsonWeakParserForString();

    private static final String FIELDS = "fields";
    private static final String VALUE_ATTR = "value";

    public CustomJiraIssueParser() {
    }

    @Override
    public Issue parse(JSONObject issueJson) throws JSONException {
        final BasicIssue basicIssue = basicIssueJsonParser.parse(issueJson);
        final JSONObject jsonFields = issueJson.getJSONObject(FIELDS);
        final JSONObject commentsJson = jsonFields.optJSONObject(COMMENT_FIELD.id);
        final Collection<Comment> comments = (commentsJson == null) ? Collections.<Comment>emptyList()
                : parseArray(commentsJson, commentJsonParser, "comments");
        final String summary = getFieldStringValue(issueJson, SUMMARY_FIELD.id);
        final String description = getOptionalFieldStringUnisex(issueJson, DESCRIPTION_FIELD.id);

        final Collection<Attachment> attachments = parseOptionalArray(issueJson, attachmentJsonParser, FIELDS, ATTACHMENT_FIELD.id);
//        final Collection<IssueField> fields = parseFields(issueJson);

        final IssueType issueType = issueTypeJsonParser.parse(getFieldUnisex(issueJson, ISSUE_TYPE_FIELD.id));
        final DateTime creationDate = JsonParseUtil.parseDateTime(getFieldStringUnisex(issueJson, CREATED_FIELD.id));
        final DateTime updateDate = JsonParseUtil.parseDateTime(getFieldStringUnisex(issueJson, UPDATED_FIELD.id));

        final String dueDateString = getOptionalFieldStringUnisex(issueJson, DUE_DATE_FIELD.id);
        final DateTime dueDate = dueDateString == null ? null : JsonParseUtil.parseDateTimeOrDate(dueDateString);

        final BasicPriority priority = getOptionalNestedField(issueJson, PRIORITY_FIELD.id, priorityJsonParser);
        final Resolution resolution = getOptionalNestedField(issueJson, RESOLUTION_FIELD.id, resolutionJsonParser);
        final User assignee = getOptionalNestedField(issueJson, ASSIGNEE_FIELD.id, userJsonParser);
        final User reporter = getOptionalNestedField(issueJson, REPORTER_FIELD.id, userJsonParser);

        final BasicProject project = projectJsonParser.parse(getFieldUnisex(issueJson, PROJECT_FIELD.id));

        final Collection<IssueLink> issueLinks = Collections.emptyList();
//        issueLinks = parseOptionalArray(issueJson, new JsonWeakParserForJsonObject<IssueLink>(issueLinkJsonParserV5), FIELDS, LINKS_FIELD.id);

        Collection<Subtask> subtasks = Collections.emptyList();
                //parseOptionalArray(issueJson, subtaskJsonParser, FIELDS, SUBTASKS_FIELD.id);

        final BasicVotes votes = null; //getOptionalNestedField(issueJson, VOTES_FIELD.id, votesJsonParser);
        final Status status = statusJsonParser.parse(getFieldUnisex(issueJson, STATUS_FIELD.id));

        final Collection<Version> fixVersions = parseOptionalArray(issueJson, versionJsonParser, FIELDS, FIX_VERSIONS_FIELD.id);
        final Collection<Version> affectedVersions = parseOptionalArray(issueJson, versionJsonParser, FIELDS, AFFECTS_VERSIONS_FIELD.id);
        final Collection<BasicComponent> components = Collections.emptyList();//parseOptionalArray(issueJson, new JsonWeakParserForJsonObject<BasicComponent>(basicComponentJsonParser), FIELDS, COMPONENTS_FIELD.id);

        final Collection<Worklog> worklogs;
        final URI selfUri = basicIssue.getSelf();

        final String transitionsUriString;
        if (issueJson.has(IssueFieldId.TRANSITIONS_FIELD.id)) {
            Object transitionsObj = issueJson.get(IssueFieldId.TRANSITIONS_FIELD.id);
            transitionsUriString = (transitionsObj instanceof String) ? (String) transitionsObj : null;
        } else {
            transitionsUriString = getOptionalFieldStringUnisex(issueJson, IssueFieldId.TRANSITIONS_FIELD.id);
        }
        final URI transitionsUri = parseTransisionsUri(transitionsUriString, selfUri);

        if (JsonParseUtil.getNestedOptionalObject(issueJson, FIELDS, WORKLOG_FIELD.id) != null) {
            worklogs = parseOptionalArray(issueJson,
                    new WorklogJsonParserV5(selfUri),
                    FIELDS, WORKLOG_FIELD.id, WORKLOGS_FIELD.id);
        } else {
            worklogs = Collections.emptyList();
        }


        final BasicWatchers watchers = null; //getOptionalNestedField(issueJson, WATCHER_FIELD.id, watchersJsonParser);
        final TimeTracking timeTracking = getOptionalNestedField(issueJson, TIMETRACKING_FIELD.id, new TimeTrackingJsonParserV5());

        final Set<String> labels = Collections.emptySet();
           //Sets.newHashSet(parseOptionalArrayNotNullable(issueJson, jsonWeakParserForString, FIELDS, LABELS_FIELD.id));

        final Collection<ChangelogGroup> changelog = parseOptionalArray(issueJson, changelogJsonParser, "changelog", "histories");
        final Operations operations = parseOptionalJsonObject(issueJson, "operations", operationsJsonParser);

        return new Issue(summary, selfUri, basicIssue.getKey(), basicIssue.getId(), project, issueType, status,
                description, priority, resolution, attachments, reporter, assignee, creationDate, updateDate,
                dueDate, affectedVersions, fixVersions, components, timeTracking, Collections.emptyList(), comments,
                transitionsUri, issueLinks,
                votes, worklogs, watchers, Collections.emptyList(), subtasks, changelog, operations, labels);
    }


    private <T> Collection<T> parseArray(final JSONObject jsonObject, final JsonParser<JSONObject, T> jsonParser, final String arrayAttribute)
            throws JSONException {
//        String type = jsonObject.getString("type");
//        final String name = jsonObject.getString("name");
        final JSONArray valueObject = jsonObject.optJSONArray(arrayAttribute);
        if (valueObject == null) {
            return new ArrayList<T>();
        }
        Collection<T> res = new ArrayList<T>(valueObject.length());
        for (int i = 0; i < valueObject.length(); i++) {
            res.add(jsonParser.parse((JSONObject) valueObject.get(i)));
        }
        return res;
    }

    private String getFieldStringValue(final JSONObject json, final String attributeName) throws JSONException {
        final JSONObject fieldsJson = json.getJSONObject(FIELDS);

        final Object summaryObject = fieldsJson.get(attributeName);
        if (summaryObject instanceof JSONObject) { // pre JIRA 5.0 way
            return ((JSONObject) summaryObject).getString(VALUE_ATTR);
        }
        if (summaryObject instanceof String) { // JIRA 5.0 way
            return (String) summaryObject;
        }
        throw new JSONException("Cannot parse [" + attributeName + "] from available fields");
    }

    @Nullable
    private String getOptionalFieldStringUnisex(final JSONObject json, final String attributeName)
            throws JSONException {
        final JSONObject fieldsJson = json.getJSONObject(FIELDS);
        return JsonParseUtil.getOptionalString(fieldsJson, attributeName);
    }

    private JSONObject getFieldUnisex(final JSONObject json, final String attributeName) throws JSONException {
        final JSONObject fieldsJson = json.getJSONObject(FIELDS);
        final JSONObject fieldJson = fieldsJson.getJSONObject(attributeName);
        if (fieldJson.has(VALUE_ATTR)) {
            return fieldJson.getJSONObject(VALUE_ATTR); // pre 5.0 way
        } else {
            return fieldJson; // JIRA 5.0 way
        }
    }

    private String getFieldStringUnisex(final JSONObject json, final String attributeName) throws JSONException {
        final JSONObject fieldsJson = json.getJSONObject(FIELDS);
        final Object fieldJson = fieldsJson.get(attributeName);
        if (fieldJson instanceof JSONObject) {
            return ((JSONObject) fieldJson).getString(VALUE_ATTR); // pre 5.0 way
        }
        return fieldJson.toString(); // JIRA 5.0 way
    }

    @Nullable
    private <T> T getOptionalNestedField(final JSONObject s, final String fieldId, final JsonObjectParser<T> jsonParser)
            throws JSONException {
        final JSONObject fieldJson = JsonParseUtil.getNestedOptionalObject(s, FIELDS, fieldId);
        // for fields like assignee (when unassigned) value attribute may be missing completely
        if (fieldJson != null) {
            return jsonParser.parse(fieldJson);
        }
        return null;
    }


    @Nullable
    private <T> Collection<T> parseOptionalArray(final JSONObject json, final JsonObjectParser<T> jsonParser, final String... path)
            throws JSONException {
        final JSONArray jsonArray = JsonParseUtil.getNestedOptionalArray(json, path);
        if (jsonArray == null) {
            return null;
        }
        final Collection<T> res = new ArrayList<T>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            res.add(jsonParser.parse((JSONObject) jsonArray.get(i)));
        }
        return res;
    }


    private URI parseTransisionsUri(final String transitionsUriString, final URI selfUri) {
        return transitionsUriString != null
                ? JsonParseUtil.parseURI(transitionsUriString)
                : UriBuilder.fromUri(selfUri).path("transitions").queryParam("expand", "transitions.fields").build();
    }
}
