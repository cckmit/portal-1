package ru.protei.portal.ui.common.client.widget.selector.report.youtrackwork;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.YoutrackWorkLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class YoutrackWorkTypeSelector extends ButtonPopupSingleSelector<En_YoutrackWorkType> {

    @Inject
    public void init( YoutrackWorkTypeModel model ) {
        setModel( model );
        setItemRenderer( option -> option == null ? defaultValue : typeLang.getTypeName(option) );
        setSearchEnabled(false);
        setHasNullValue(false);
        setDefaultValue(lang.unknownField());
    }

    @Inject
    YoutrackWorkLang typeLang;

    @Inject
    Lang lang;
}
