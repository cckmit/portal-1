package ru.protei.portal.ui.common.client.widget.issueimportance;

import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.List;

/**
 * Селектор критичности обращения
 */
public class ImportanceBtnGroupMulti extends ToggleBtnGroupMulti<ImportanceLevel> {
    public void fillButtons(List<ImportanceLevel> importanceLevelList) {
        clear();

        for (ImportanceLevel level : importanceLevelList) {
            addBtnWithIconAndTooltip(
                    ImportanceStyleProvider.getImportanceIcon(level.getCode()) + " center",
                    "btn btn-default no-border " + level.toString().toLowerCase(),
                    level.getCode(),
                    level
            );
            setEnsureDebugId(level, DebugIdsHelper.IMPORTANCE_BUTTON.byCode(level.getCode()));
        }
    }
}
