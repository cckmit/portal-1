package ru.protei.portal.ui.delivery.client.widget.card.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.lang.CardStateLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

/**
 * Селектор списка состояний плат
 */
public class CardStatesOptionList extends OptionList<CaseState> implements SelectorWithModel<CaseState> {

    @Inject
    public void init(CardStateOptionsModel stateModel, CardStateLang lang) {
        this.lang = lang;
        setSelectorModel(stateModel);
    }

    @Override
    public void fillOptions(List<CaseState> states) {
        clearOptions();
        states.forEach(state -> {
            addOption(lang.getStateName(state), state, "inline m-r-5", state.getInfo(), state.getColor());
            setEnsureDebugId(state, DebugIdsHelper.ISSUE_STATE.byId(state.getId()));
        });
    }

    @Override
    public void refreshValue() {}

    private CardStateLang lang;
}
