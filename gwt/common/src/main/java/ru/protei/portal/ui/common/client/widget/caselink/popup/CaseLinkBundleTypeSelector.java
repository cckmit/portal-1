package ru.protei.portal.ui.common.client.widget.caselink.popup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_BundleType;
import ru.protei.portal.ui.common.client.lang.En_BundleTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class CaseLinkBundleTypeSelector extends ButtonPopupSingleSelector<En_BundleType> {

    @Inject
    public void init(CaseLinkBundleTypeModel caseLinkBundleTypeModel) {
        setModel(caseLinkBundleTypeModel);
        setSearchEnabled(false);
        setItemRenderer(value -> value == null ? defaultValue : bundleTypeLang.getName(value));
        setDisplayStyle("btn btn-default btn-block dropdown-toggle");
    }

    @Inject
    En_BundleTypeLang bundleTypeLang;
}
