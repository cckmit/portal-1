package ru.protei.portal.jira.struct;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class JiraExtAppData {

    private String issueType;
    private String severity;
    private Set<Long> commentIds;
    private Set<String> attachmentIds;

    public JiraExtAppData() {
        commentIds = new HashSet<>();
        attachmentIds = new HashSet<>();
    }

    public JiraExtAppData setIssueType(String issueType) {
        this.issueType = issueType;
        return this;
    }

    public JiraExtAppData setSeverity(String severity) {
        this.severity = severity;
        return this;
    }

    public JiraExtAppData appendComment (long id) {
        commentIds.add(id);
        return this;
    }

    public JiraExtAppData appendAttachment (String id) {
        attachmentIds.add(id);
        return this;
    }

    public JiraExtAppData appendAttachment (URI uri) {
        attachmentIds.add(uri.toString());
        return this;
    }

    @Override
    public String toString() {
        return toJSON(this);
    }

    public String issueType() {
        return issueType;
    }

    public String severity() {
        return severity;
    }

    public int commentsCount () {
        return commentIds.size();
    }

    public int attachmentsCount () {
        return attachmentIds.size();
    }

    public boolean hasComment (long id) {
        return commentIds.contains(id);
    }

    public boolean hasAttachment (String id) {
        return attachmentIds.contains(id);
    }

    public boolean hasAttachment (URI uri) {
        return attachmentIds.contains(uri.toString());
    }

    public static String toJSON (JiraExtAppData state) {
        try {
            JSONObject jsObj = new JSONObject();
            jsObj.put("cid", new JSONArray(state.commentIds));
            jsObj.put("aid", new JSONArray(state.attachmentIds));
            jsObj.put("issueType", state.issueType);
            jsObj.put("severity", state.severity);
            return jsObj.toString();
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JiraExtAppData fromJSON (String val) {
        JiraExtAppData state = new JiraExtAppData();

        if (val == null || val.isEmpty())
            return state;

        try {
            JSONObject json = new JSONObject(val);

            readArrayOfLong(json, "cid", state.commentIds);
            readArrayOfString(json, "aid", state.attachmentIds);
            state.issueType = readString(json, "issueType");
            state.severity = readString(json, "severity");

            return state;
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    private static void readArrayOfLong (JSONObject obj, String key, Collection<Long> to) throws JSONException {
        if (!obj.has(key))
            return;

        JSONArray array = obj.getJSONArray(key);
        for (int i = 0; i < array.length(); i++)
            to.add(array.getLong(i));
    }

    private static void readArrayOfString (JSONObject obj, String key, Collection<String> to) throws JSONException {
        if (!obj.has(key))
            return;

        JSONArray array = obj.getJSONArray(key);
        for (int i = 0; i < array.length(); i++)
            to.add(array.getString(i));
    }

    private static String readString (JSONObject obj, String key) throws JSONException {
        if (!obj.has(key))
            return null;

        return obj.getString(key);
    }
}
