package ru.protei.portal.ui.common.client.widget.document.doctype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class DocumentTypeSelector
        extends ButtonSelector<DocumentType>
        implements SelectorWithModel<DocumentType> {

    @Inject
    void init(DocumentTypeModel documentTypeModel) {
        documentTypeModel.subscribe(this);
        setSearchEnabled(false);

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
