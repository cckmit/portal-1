package ru.protei.portal.ui.common.client.widget.document.doccategory;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;

public abstract class DocumentCategoryModel implements SelectorModel<En_DocumentCategory>, Activity {
    @Override
    public En_DocumentCategory get(int elementIndex) {
        if (elementIndex < En_DocumentCategory.values().length) {
            return En_DocumentCategory.values()[elementIndex];
        }

        return null;
    }
}
