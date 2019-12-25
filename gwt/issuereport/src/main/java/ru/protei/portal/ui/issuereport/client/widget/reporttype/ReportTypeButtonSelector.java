package ru.protei.portal.ui.issuereport.client.widget.reporttype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.ui.common.client.lang.En_ReportTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ReportTypeButtonSelector extends ButtonSelector<En_ReportType> {

    @Inject
    public void init() {
        setDisplayOptionCreator(o -> new DisplayOption(lang.getType(o)));
    }

    public void fillOptions(List<En_ReportType> items) {
        clearOptions();
        items.forEach(this::addOption);
    }

    @Inject
    private En_ReportTypeLang lang;
}
