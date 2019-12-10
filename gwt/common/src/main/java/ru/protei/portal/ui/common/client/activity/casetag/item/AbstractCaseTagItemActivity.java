package ru.protei.portal.ui.common.client.activity.casetag.item;

import ru.protei.portal.core.model.ent.CaseTag;

/**
 * Абстрактная активность тега
 */
public interface AbstractCaseTagItemActivity {

    void onDetachClicked(AbstractCaseTagItemView itemView);

    void onAttachTagClicked(CaseTag value);
}
