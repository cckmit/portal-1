package ru.protei.portal.ui.delivery.client.widget.cardbatch.importance;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.StringUtils.firstUppercaseChar;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;

/**
 * Селектор критичности партии плат
 */
public class CardBatchImportanceBtnGroupMulti extends ToggleBtnGroupMulti<ImportanceLevel> implements SelectorWithModel<ImportanceLevel> {
    @Inject
    public void init(CardBatchImportanceBtnGroupModel importanceModel) {
        importanceModel.subscribe(this);
    }

    public void fillOptions(List<ImportanceLevel> importanceLevelList) {
        clear();

        for (ImportanceLevel level : importanceLevelList) {
            if (CARD_BATCH_IMPORTANCE_IDS.contains(level.getId())) {
                addBtnWithIconAndTooltip(
                        "case-importance",
                        "btn btn-default no-border",
                        level.getCode(),
                        firstUppercaseChar(level.getCode()),
                        level,
                        level.getColor(),
                        makeContrastColor(level.getColor())
                );
                setEnsureDebugId(level, DebugIdsHelper.IMPORTANCE_BUTTON.byId(level.getId()));
            }
        }
    }

    private static final List<Integer> CARD_BATCH_IMPORTANCE_IDS = new ArrayList<>(Arrays.asList(1, 2, 3, 8));
}
