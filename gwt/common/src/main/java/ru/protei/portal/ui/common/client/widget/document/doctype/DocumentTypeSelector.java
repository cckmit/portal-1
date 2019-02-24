package ru.protei.portal.ui.common.client.widget.document.doctype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public void fillOptions(List<DocumentType> options) {
        this.options = options;

        if (categoryFilter != null) {
            options = options
                    .stream()
                    .filter(d -> d.getDocumentCategory() == categoryFilter)
                    .collect(Collectors.toList());
        }
        clearOptions();
        if (defaultValue != null) {
            addOption(null);
            setValue(null);
        }
        options.forEach(this::addOption);
    }

    public void setCategoryFilter(En_DocumentCategory category) {
        this.categoryFilter = category;
        fillOptions(options);
    }

    private En_DocumentCategory categoryFilter = null;

    private List<DocumentType> options = new LinkedList<>();

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private String defaultValue = null;
}
