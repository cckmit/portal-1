package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseComment;

public class ReportProjectWithLastComment {
    private Project project;
    private CaseComment lastComment;

    public ReportProjectWithLastComment(Project project, CaseComment lastComment) {
        this.project = project;
        this.lastComment = lastComment;
    }

    public Project getProject() {
        return project;
    }

    public CaseComment getLastComment() {
        return lastComment;
    }

    @Override
    public String toString() {
        return "ReportProjects{" +
                "project=" + project +
                ", lastComment=" + lastComment +
                '}';
    }
}
