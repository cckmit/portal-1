package ru.protei.portal.redmine.factories;

import com.taskadapter.redmineapi.bean.Issue;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.redmine.api.RedmineStatus;
import ru.protei.portal.redmine.handlers.MergeHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class MergeHandlerFactoryImpl implements MergeHandlerFactory {
    public MergeHandlerFactoryImpl() {
        mergeHandlers.add(new TrivialMergeHandler());
    }

    @Override
    public CaseObject mergeWithCaseObject(Issue issue, CaseObject object) {
        mergeHandlers.forEach(x -> x.merge(issue, object));
        return object;
    }

    public class TrivialMergeHandler implements MergeHandler {
        @Override
        public void merge(Issue issue, CaseObject object) {
            issue.getPriorityId();
            issue.getStatusName();
            issue.getSubject();
            object.setImpLevel(issue.getPriorityId());
            object.setName(issue.getSubject());
            object.setState(RedmineStatus.valueOf(issue.getStatusName()).getCaseState());
            object.setStateId(issue.getStatusId());
        }
    }

    private final List<MergeHandler> mergeHandlers = new LinkedList<>();
}
