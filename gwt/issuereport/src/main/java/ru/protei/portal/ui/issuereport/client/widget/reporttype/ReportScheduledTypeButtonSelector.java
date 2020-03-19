package ru.protei.portal.ui.issuereport.client.widget.reporttype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.ui.common.client.lang.En_ReportScheduledTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ReportScheduledTypeButtonSelector extends ButtonSelector<En_ReportScheduledType> {

    @Inject
    public void init() {
        setDisplayOptionCreator(o -> new DisplayOption(lang.getType(o)));
    }

    public void fillOptions(List<En_ReportScheduledType> items) {
        clearOptions();
        items.forEach(this::addOption);
    }

    @Inject
    private En_ReportScheduledTypeLang lang;
}
