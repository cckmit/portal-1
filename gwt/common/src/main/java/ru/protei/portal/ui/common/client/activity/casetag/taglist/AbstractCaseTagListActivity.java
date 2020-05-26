package ru.protei.portal.ui.common.client.activity.casetag.taglist;

import ru.protei.portal.core.model.ent.CaseTag;

/**
 * Абстрактная активность списка тегов
 */
public interface AbstractCaseTagListActivity {
    void onTagAttach(CaseTag caseTag);
}
