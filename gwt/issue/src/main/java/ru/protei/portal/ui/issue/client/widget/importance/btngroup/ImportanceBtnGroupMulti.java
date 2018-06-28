package ru.protei.portal.ui.issue.client.widget.importance.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

/**
 * Селектор критичности обращения
 */
public class ImportanceBtnGroupMulti extends ToggleBtnGroupMulti<En_ImportanceLevel> {

    @Inject
    public void init() {
        fillButtons();
    }

    public void fillButtons() {
        clear();

        for (En_ImportanceLevel type : En_ImportanceLevel.values()) {
            addBtnWithIcon(
                    ImportanceStyleProvider.getImportanceIcon(type) + " center",
                    "btn btn-white btn-without-border " + type.toString().toLowerCase(),
                    null,
                    type
            );
        }
    }

    @Inject
    En_CaseImportanceLang lang;
}