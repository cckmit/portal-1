package ru.protei.portal.ui.common.client.widget.document.doccategory;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.ui.common.client.lang.En_DocumentCategoryLang;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.Arrays;
import java.util.List;

public class DocumentCategoryFormSelector extends FormSelector<En_DocumentCategory> implements SelectorWithModel<En_DocumentCategory> {

    @Inject
    void init() {
        setSearchEnabled(false);
        setHasNullValue(true);

        setDisplayOptionCreator(val -> {
            if (val == null) {
                return new DisplayOption(defaultValue);
            }
            return new DisplayOption(lang.getDocumentCategoryName(val));
        });

        fillOptions(Arrays.asList(En_DocumentCategory.values()));
    }

    @Override
    public void fillOptions(List<En_DocumentCategory> options) {
        clearOptions();
        if (defaultValue != null) {
            addOption(null);
        }
        options.forEach(this::addOption);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private String defaultValue = null;

    @Inject
    En_DocumentCategoryLang lang;
}
