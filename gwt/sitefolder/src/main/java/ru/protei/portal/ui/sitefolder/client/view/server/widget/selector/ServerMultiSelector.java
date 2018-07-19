package ru.protei.portal.ui.sitefolder.client.view.server.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

public class ServerMultiSelector extends MultipleInputSelector<EntityOption> implements ModelSelector<EntityOption> {

    @Inject
    public void init() {
        serverModel.subscribe(this);
        setAddName(lang.buttonAdd());
    }

    public void fillOptions(List<EntityOption> options) {
        clearOptions();
        for (EntityOption option : options) {
            addOption(option.getDisplayText(), option);
        }
    }

    @Override
    public void refreshValue() {}

    @Inject
    ServerModel serverModel;
    @Inject
    Lang lang;
}
