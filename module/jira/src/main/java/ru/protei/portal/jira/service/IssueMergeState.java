package ru.protei.portal.jira.service;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class IssueMergeState {
    Set<Long> commentIds;
    Set<String> attachmentIds;

    public IssueMergeState () {
        commentIds = new HashSet<>();
        attachmentIds = new HashSet<>();
    }

    public IssueMergeState appendComment (long id) {
        commentIds.add(id);
        return this;
    }

    public IssueMergeState appendAttachment (String id) {
        attachmentIds.add(id);
        return this;
    }

    public IssueMergeState appendAttachment (URI uri) {
        attachmentIds.add(uri.toString());
        return this;
    }

    @Override
    public String toString() {
        return toJSON(this);
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

    public static String toJSON (IssueMergeState state) {
        try {
            JSONObject jsObj = new JSONObject();
            jsObj.put("cid", new JSONArray(state.commentIds));
            jsObj.put("aid", new JSONArray(state.attachmentIds));
            return jsObj.toString();
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    public static IssueMergeState fromJSON (String val) {
        IssueMergeState state = new IssueMergeState();

        if (val == null || val.isEmpty())
            return state;

        try {
            JSONObject json = new JSONObject(val);

            readArrayOfLong(json, "cid", state.commentIds);
            readArrayOfString(json, "aid", state.attachmentIds);

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
}
