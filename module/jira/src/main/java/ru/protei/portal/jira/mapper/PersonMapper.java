package ru.protei.portal.jira.mapper;

import com.atlassian.jira.rest.client.api.domain.User;
import ru.protei.portal.core.model.ent.Person;

public interface PersonMapper {
    Person toProteiPerson (User jiraUser);
}
