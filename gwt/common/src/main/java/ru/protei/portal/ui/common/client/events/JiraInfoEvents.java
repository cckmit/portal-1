package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.util.CrmConstants;

public class JiraInfoEvents {
    /**
     * Показать справку по jira
     */
    @Url(value = CrmConstants.Jira.INFO_LINK)
    public static class Show {}
}
