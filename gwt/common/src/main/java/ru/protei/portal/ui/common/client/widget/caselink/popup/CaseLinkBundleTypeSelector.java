package ru.protei.portal.ui.common.client.widget.caselink.popup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_BundleType;
import ru.protei.portal.ui.common.client.lang.En_BundleTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

import java.util.List;

public class CaseLinkBundleTypeSelector extends ButtonPopupSingleSelector<En_BundleType> {

    @Inject
    public void init(CaseLinkBundleTypeModel caseLinkBundleTypeModel) {
        this.model = caseLinkBundleTypeModel;
        setModel(caseLinkBundleTypeModel);
        setSearchEnabled(false);
        setItemRenderer(value -> value == null ? defaultValue : bundleTypeLang.getName(value));
        setDisplayStyle("btn btn-default btn-block dropdown-toggle");
    }

    public void updateElements(List<En_BundleType> list) {
        model.updateElements(list);
    }

    CaseLinkBundleTypeModel model;

    @Inject
    En_BundleTypeLang bundleTypeLang;
}
