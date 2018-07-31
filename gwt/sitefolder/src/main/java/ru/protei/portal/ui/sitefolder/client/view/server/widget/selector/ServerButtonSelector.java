package ru.protei.portal.ui.sitefolder.client.view.server.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ServerButtonSelector extends ButtonSelector<EntityOption> implements ModelSelector<EntityOption> {

    @Inject
    void init() {
        serverModel.subscribe(this);
        setSearchEnabled(true);
        setHasNullValue(true);

        setDisplayOptionCreator(value -> new DisplayOption(value == null ? null : value.getDisplayText()));
    }

    @Override
    public void fillOptions(List<EntityOption> options) {
        clearOptions();
        options.forEach(this::addOption);
    }

    @Inject
    ServerModel serverModel;
}