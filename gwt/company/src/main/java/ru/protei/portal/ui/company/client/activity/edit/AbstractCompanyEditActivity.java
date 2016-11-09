package ru.protei.portal.ui.company.client.activity.edit;

import ru.brainworm.factory.widget.table.client.helper.ClickColumn;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.company.client.view.edit.columns.EditColumn;

/**
 * Активность создания и редактирования компании
 */
public interface AbstractCompanyEditActivity extends EditColumn.EditHandler< Person> {
    void onSaveClicked();
    void onCancelClicked();
    void onChangeCompanyName();
}
