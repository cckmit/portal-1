package ru.protei.portal.ui.common.client.widget.document.doctype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

public class DocumentTypeFormSelector extends FormSelector<DocumentType> implements SelectorWithModel<DocumentType> {

    @Inject
    void init(DocumentTypeModel model) {
        setSelectorModel(model);

        setSearchEnabled(true);
        setSearchAutoFocus(true);

        setDisplayOptionCreator(val -> {
            if (val == null) {
                return new DisplayOption(defaultValue);
            }
            String text = val.getName();
            if (HelperFunc.isNotEmpty(val.getShortName())) {
                text += " (" + val.getShortName() + ")";
            }
            return new DisplayOption(text);
        });
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private String defaultValue = null;
}
