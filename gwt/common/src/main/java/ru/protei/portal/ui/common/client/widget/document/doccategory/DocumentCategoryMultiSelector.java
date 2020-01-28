package ru.protei.portal.ui.common.client.widget.document.doccategory;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.ui.common.client.lang.En_DocumentCategoryLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.optionlist.base.ModelList;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.Arrays;
import java.util.List;

public class DocumentCategoryMultiSelector extends MultipleInputSelector<En_DocumentCategory> implements ModelList<En_DocumentCategory> {

    @Inject
    public void init(Lang lang, En_DocumentCategoryLang categoryLang) {
        this.categoryLang = categoryLang;
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        fillOptions(Arrays.asList(En_DocumentCategory.values()));
    }

    @Override
    public void fillOptions(List<En_DocumentCategory> options) {
        clearOptions();
        options.forEach(option -> addOption(categoryLang.getDocumentCategoryName(option), option));
    }

    private En_DocumentCategoryLang categoryLang;
}
