package ru.protei.portal.ui.common.client.activity.casetag.list;


import ru.protei.portal.core.model.ent.CaseTag;

/**
 * Абстрактная активность списка тегов
 */
public interface AbstractCaseTagListActivity {

    void onAddClicked();

    void onAttachTagClicked(CaseTag value);

    void onEditClicked(CaseTag caseTag, boolean isReadOnly);

}
