package ru.protei.portal.ui.delivery.client.widget.cardbatch.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.lang.CardBatchStateLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

/**
 * Селектор списка состояний партий плат
 */
public class CardBatchStatesOptionList extends OptionList<CaseState> implements SelectorWithModel<CaseState> {

    @Inject
    public void init(CardBatchStateOptionsModel stateModel, CardBatchStateLang lang) {
        this.lang = lang;
        setSelectorModel(stateModel);
    }

    @Override
    public void fillOptions(List<CaseState> states) {
        clearOptions();
        states.forEach(state -> {
            addOption(lang.getStateName(state), state, "inline m-r-5", state.getInfo(), state.getColor());
            setEnsureDebugId(state, DebugIdsHelper.CARD_BATCH_STATE.byId(state.getId()));
        });
    }

    @Override
    public void refreshValue() {}

    private CardBatchStateLang lang;
}
