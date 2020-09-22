package ru.protei.portal.ui.common.client.widget.document.doccategory;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.ui.common.client.lang.En_DocumentCategoryLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class DocumentCategoryMultiSelector extends InputPopupMultiSelector<En_DocumentCategory> {
    @Inject
    public void init(DocumentCategoryModel documentCategoryModel, Lang lang, En_DocumentCategoryLang categoryLang) {
        setModel(documentCategoryModel);
        setSearchEnabled(false);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(categoryLang::getDocumentCategoryName);
    }
}
