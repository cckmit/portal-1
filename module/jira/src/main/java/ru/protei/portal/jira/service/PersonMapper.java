package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.domain.User;
import ru.protei.portal.core.model.ent.Person;

interface PersonMapper {
    Person toProteiPerson (User jiraUser);
}
