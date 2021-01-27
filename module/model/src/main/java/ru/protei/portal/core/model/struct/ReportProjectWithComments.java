package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseComment;

import java.util.List;

public class ReportProjectWithComments {
    private Project project;
    private CaseComment lastComment;
    private List<CaseComment> comments;

    public ReportProjectWithComments(Project project, CaseComment lastComment, List<CaseComment> comments) {
        this.project = project;
        this.lastComment = lastComment;
        this.comments = comments;
    }

    public Project getProject() {
        return project;
    }

    public CaseComment getLastComment() {
        return lastComment;
    }

    public List<CaseComment> getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "ReportProjectWithLastComment{" +
                "project=" + project +
                ", lastComment=" + lastComment +
                ", comments=" + comments +
                '}';
    }
}
