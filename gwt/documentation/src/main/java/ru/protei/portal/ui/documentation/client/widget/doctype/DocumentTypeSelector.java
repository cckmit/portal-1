package ru.protei.portal.ui.documentation.client.widget.doctype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class DocumentTypeSelector
        extends ButtonSelector<DocumentType>
        implements ModelSelector<DocumentType> {

    @Inject
    void init(DocumentTypeModel documentTypeModel) {
        documentTypeModel.subscribe(this);
        setSearchEnabled(false);
        setHasNullValue(false);

        setDisplayOptionCreator(val -> {
            if (val == null) {
                return new DisplayOption(defaultValue);
            }
            return new DisplayOption(val.getName());
        });
    }

    @Override
    public void fillOptions(List<DocumentType> options) {
        clearOptions();
        if (defaultValue != null) {
            addOption(null);
            setValue(null);
        }
        options.forEach(this::addOption);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private String defaultValue = null;
}
