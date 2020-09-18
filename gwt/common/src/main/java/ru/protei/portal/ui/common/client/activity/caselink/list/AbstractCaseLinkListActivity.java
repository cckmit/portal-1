package ru.protei.portal.ui.common.client.activity.caselink.list;

import ru.protei.portal.core.model.ent.CaseLink;

/**
 * Абстрактная активность списка линков
 */
public interface AbstractCaseLinkListActivity {
    void onAddLinkClicked(CaseLink value);
}
