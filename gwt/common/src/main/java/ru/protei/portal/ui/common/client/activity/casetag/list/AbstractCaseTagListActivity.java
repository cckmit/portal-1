package ru.protei.portal.ui.common.client.activity.casetag.list;

import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;

/**
 * Абстрактная активность списка тегов
 */
public interface AbstractCaseTagListActivity {

    void onShow(CaseTagEvents.Show event);

    void onAddClicked();

    void onAttachTagClicked(CaseTag value);

    void onEditClicked(CaseTag caseTag, boolean isReadOnly);

}
