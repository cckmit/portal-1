package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JiraExtAppData {

    @JsonProperty("issueType")
    private String issueType;
    @JsonProperty("sla-severity")
    private String slaSeverity;
    @JsonProperty("cid")
    private Set<Long> commentIds;
    @JsonProperty("aid")
    private Set<String> attachmentIds;
    @JsonProperty("clmId")
    private String clmId;

    public JiraExtAppData() {
        commentIds = new HashSet<>();
        attachmentIds = new HashSet<>();
    }

    public JiraExtAppData setIssueType(String issueType) {
        this.issueType = issueType;
        return this;
    }

    public JiraExtAppData setSlaSeverity(String slaSeverity) {
        this.slaSeverity = slaSeverity;
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

    public JiraExtAppData setClmId(String clmId) {
        this.clmId = clmId;
        return this;
    }

    @Override
    public String toString() {
        return toJSON(this);
    }

    public String issueType() {
        return issueType;
    }

    public String slaSeverity() {
        return slaSeverity;
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

    public String clmId() {
        return clmId;
    }

    public static String toJSON (JiraExtAppData state) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(state);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JiraExtAppData fromJSON (String val) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(val, JiraExtAppData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
