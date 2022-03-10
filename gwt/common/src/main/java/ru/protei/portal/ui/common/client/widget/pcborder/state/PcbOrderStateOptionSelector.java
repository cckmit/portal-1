package ru.protei.portal.ui.common.client.widget.pcborder.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderStateLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

import java.util.stream.Stream;

public class PcbOrderStateOptionSelector extends OptionList<En_PcbOrderState> {

    @Inject
    public void init() {
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();
        Stream.of(En_PcbOrderState.values()).forEach(state -> {
            addOption(lang.getStateName(state), state, "inline m-r-5", lang.getStateName(state), state.getColor());
            setEnsureDebugId(state, DebugIdsHelper.PCB_ORDER_STATE.byId(state.getId()));
        });
    }

    @Inject
    En_PcbOrderStateLang lang;
}
