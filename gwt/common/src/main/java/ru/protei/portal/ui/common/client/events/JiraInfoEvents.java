package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class JiraInfoEvents {
    /**
     * Показать справку по jira
     */
    @Url(value = "jiraInfo")
    public static class Show {}
}
