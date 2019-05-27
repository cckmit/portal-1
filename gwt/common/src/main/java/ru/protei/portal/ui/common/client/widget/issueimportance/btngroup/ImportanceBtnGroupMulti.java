package ru.protei.portal.ui.common.client.widget.issueimportance.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.test.client.DebugIdsHelper;
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
                    "btn btn-default no-border " + type.toString().toLowerCase(),
                    null,
                    type
            );
            setEnsureDebugId(type, DebugIdsHelper.IMPORTANCE_BUTTON.byId(type.getId()));
        }
    }

    @Inject
    En_CaseImportanceLang lang;
}