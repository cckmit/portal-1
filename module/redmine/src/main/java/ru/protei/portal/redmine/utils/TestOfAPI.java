package ru.protei.portal.redmine.utils;

import com.taskadapter.redmineapi.Params;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;

public class TestOfAPI {
    public void someSideEffectFunc() {
        RedmineManager manager = RedmineManagerFactory.createWithApiKey("a", "b");
        Params params = new Params()
                .add();
        Issue issue = manager.getIssueManager().getIssues();
    }
}
