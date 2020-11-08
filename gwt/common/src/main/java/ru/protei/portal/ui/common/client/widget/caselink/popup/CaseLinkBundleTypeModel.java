package ru.protei.portal.ui.common.client.widget.caselink.popup;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_BundleType;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;

import java.util.List;

public abstract class CaseLinkBundleTypeModel extends BaseSelectorModel<En_BundleType> implements Activity {

    public void updateElements(List<En_BundleType> result) {
        super.updateElements(result);
    }
}
