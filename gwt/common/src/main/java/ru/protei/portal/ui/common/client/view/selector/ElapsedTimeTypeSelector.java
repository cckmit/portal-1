package ru.protei.portal.ui.common.client.view.selector;

import com.google.gwt.dom.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class ElapsedTimeTypeSelector extends ButtonSelector<En_TimeElapsedType> {

    @Inject
    public void init() {
        setDisplayOptionCreator( type -> new DisplayOption( type != null ? lang.getName( type ) : lang.getName( En_TimeElapsedType.NONE ) ) );
        fillOptions();
    }

    public Element getPopupContainer() {
        return popup.getElement();
    }

    public void fillOptions() {
        clearOptions();

        for (En_TimeElapsedType type : En_TimeElapsedType.values())
            addOption( type );
    }

    @Inject
    private TimeElapsedTypeLang lang;
}
