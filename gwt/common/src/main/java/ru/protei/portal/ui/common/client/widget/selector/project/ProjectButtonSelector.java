package ru.protei.portal.ui.common.client.widget.selector.project;

import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ProjectButtonSelector
        extends ButtonSelector<ProjectInfo>
        implements ModelSelector<ProjectInfo> {

    @Inject
    public void init(ProjectModel model) {
        model.subscribe(this);
        setSearchEnabled(true);
        setHasNullValue(false);
        setDisplayOptionCreator(val ->
                new DisplayOption(val == null ? defaultValue : val.getName())
        );
    }

    @Override
    public void fillOptions(List<ProjectInfo> options) {
        clearOptions();
        options.forEach(this::addOption);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private String defaultValue;
}
