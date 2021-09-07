package ru.protei.portal.ui.common.client.widget.selector.report.youtrackwork;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.YoutrackProject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ReportYoutrackWorkYtProjectMultiSelector extends InputPopupMultiSelector<YoutrackProject> {
    public void clean() {
        if (model != null) {
            model.clean();
        }
    }

    @Inject
    void init(ReportYtWorkYtProjectMultiModel model, Lang lang) {
        this.model = model;

        setAsyncModel(model);

        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());

        setSearchEnabled(true);

        setItemRenderer(YoutrackProject::getShortName);
    }

    private ReportYtWorkYtProjectMultiModel model;
}
