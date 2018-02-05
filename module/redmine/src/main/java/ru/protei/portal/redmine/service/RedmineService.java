package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;

import java.util.Date;
import java.util.List;

public interface RedmineService {
    Issue getIssueById(int id) throws RedmineException;

    List<Issue> getIssuesAfterDate(Date date) throws RedmineException;

    List<Issue> getIssuesBeforeDate(Date date) throws RedmineException;

    List<Issue> getIssuesInDateRange(Date start, Date end) throws RedmineException;
}
