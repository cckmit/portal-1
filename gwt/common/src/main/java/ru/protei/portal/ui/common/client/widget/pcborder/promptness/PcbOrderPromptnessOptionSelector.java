package ru.protei.portal.ui.common.client.widget.pcborder.promptness;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderPromptness;
import ru.protei.portal.test.client.DebugIdsHelper;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderPromptnessLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

import java.util.stream.Stream;

public class PcbOrderPromptnessOptionSelector extends OptionList<En_PcbOrderPromptness> {

    @Inject
    public void init() {
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();
        Stream.of(En_PcbOrderPromptness.values()).forEach(state -> {
            addOption(lang.getName(state), state, "inline m-r-5", lang.getName(state), state.getColor());
            setEnsureDebugId(state, DebugIdsHelper.PCB_ORDER_PROMPTNESS.byId(state.getId()));
        });
    }

    @Inject
    En_PcbOrderPromptnessLang lang;
}
