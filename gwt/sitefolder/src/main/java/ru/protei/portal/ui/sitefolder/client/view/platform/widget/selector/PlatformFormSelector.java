package ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

public class PlatformFormSelector extends FormSelector<PlatformOption> implements SelectorWithModel<PlatformOption> {

    @Inject
    void init(PlatformModel model) {
        setSelectorModel(model);
        setSearchEnabled(true);
        setHasNullValue(true);

        setDisplayOptionCreator(value -> {
            DisplayOption displayOption = new DisplayOption(value == null ? defaultValue : value.getDisplayText());
            displayOption.setExternalLink(value == null ? null : LinkUtils.makeLink(Platform.class, value.getId()));

            return displayOption;
        });
    }

    @Override
    public void fillOptions(List<PlatformOption> options) {
        clearOptions();

        if (defaultValue != null) {
            addOption(null);
        }

        options.forEach(this::addOption);
        reselectValueIfNeeded();
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private String defaultValue = null;
}
