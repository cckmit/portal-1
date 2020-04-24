package ru.protei.portal.ui.common.client.widget.selector.project;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

public class ProjectFormSelector extends FormSelector<EntityOption> implements SelectorWithModel<EntityOption> {

    @Inject
    void init(ProjectModel model) {
        this.model = model;
        setSelectorModel(model);
        setSearchEnabled(true);
        setHasNullValue(true);
        setDisplayOptionCreator(val ->
                new DisplayOption(val == null ? defaultValue : val.getDisplayText())
        );
    }

    @Override
    public void fillOptions(List<EntityOption> options) {
        clearOptions();
        if (defaultValue != null) {
            addOption(null);
        }
        options.forEach(this::addOption);
    }

    @Override
    public boolean requestByOnLoad() {
        return requestByOnLoad;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setPlatformIndependentProject(Boolean platformIndependentProject) {
        model.setPlatformIndependentProject(platformIndependentProject);
    }

    public void setRequestByOnLoad(boolean requestByOnLoad) {
        this.requestByOnLoad = requestByOnLoad;
    }

    private boolean requestByOnLoad = true;
    private ProjectModel model;
    private String defaultValue;
}
