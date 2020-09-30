package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.RedmineEndpoint;

import java.util.Collection;
import java.util.List;

public interface RedmineService {
    Result<Issue> getIssueById( int id, RedmineEndpoint endpoint);

    Result<Issue> updateIssue( Issue issue, RedmineEndpoint endpoint);

    Result<List<com.taskadapter.redmineapi.bean.Attachment>> uploadAttachment( Collection<Attachment> attachment, RedmineEndpoint endpoint);

    Result<List<Issue>> getNewIssues( RedmineEndpoint endpoint );

    Result<List<Issue>> getUpdatedIssues( RedmineEndpoint endpoint );

    Result<User> getUser( int id, RedmineEndpoint endpoint );


}
