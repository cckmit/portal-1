package ru.protei.portal.ui.common.client.widget.selector.report.additionalparams;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportAdditionalParamType;
import ru.protei.portal.ui.common.client.lang.En_ReportAdditionalParamTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ReportAdditionalParamsMultiSelector extends InputPopupMultiSelector<En_ReportAdditionalParamType> {
    @Inject
    void init(ReportAdditionalParamsModel model, En_ReportAdditionalParamTypeLang reportAdditionalParamTypeLang, Lang lang) {
        setModel(model);

        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());

        setSearchEnabled(false);

        setItemRenderer(reportAdditionalParamTypeLang::getName);
    }
}
