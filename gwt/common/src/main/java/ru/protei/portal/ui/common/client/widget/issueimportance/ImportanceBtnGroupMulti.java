package ru.protei.portal.ui.common.client.widget.issueimportance;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.ArrayList;
import java.util.List;

/**
 * Селектор критичности обращения
 */
public class ImportanceBtnGroupMulti extends ToggleBtnGroupMulti<En_ImportanceLevel> {

    @Inject
    public void init() {
        fillButtons();
    }

    public void fillButtons() {

        List<En_ImportanceLevel> orderedImportanceLevelList = new ArrayList<>();
        orderedImportanceLevelList.add(En_ImportanceLevel.EMERGENCY);
        orderedImportanceLevelList.add(En_ImportanceLevel.CRITICAL);
        orderedImportanceLevelList.add(En_ImportanceLevel.IMPORTANT);
        orderedImportanceLevelList.add(En_ImportanceLevel.MEDIUM);
        orderedImportanceLevelList.add(En_ImportanceLevel.BASIC);
        orderedImportanceLevelList.add(En_ImportanceLevel.COSMETIC);

        fillButtons(orderedImportanceLevelList);
    }

    public void fillButtons(List<En_ImportanceLevel> importanceLevelList) {
        clear();

        for (En_ImportanceLevel level : importanceLevelList) {
            addBtnWithIconAndTooltip(
                    ImportanceStyleProvider.getImportanceIcon(level) + " center",
                    "btn btn-default no-border " + level.toString().toLowerCase(),
                    lang.getImportanceName(level),
                    level
            );
            setEnsureDebugId(level, DebugIdsHelper.IMPORTANCE_BUTTON.byId(level.getId()));
        }
    }

    @Inject
    En_CaseImportanceLang lang;
}