package ru.protei.portal.ui.common.client.widget.selector.report.youtrackwork;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.ReportYoutrackWorkLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class ReportYoutrackDictionarySelector extends ButtonPopupSingleSelector<En_ReportYoutrackWorkType> {

    @Inject
    public void init( ReportYoutrackDictionaryModel model ) {
        setModel( model );
        setItemRenderer( option -> option == null ? defaultValue : typeLang.getTypeName(option) );
        setSearchEnabled(false);
        setHasNullValue(false);
        setDefaultValue(lang.unknownField());
    }

    @Inject
    ReportYoutrackWorkLang typeLang;

    @Inject
    Lang lang;
}
