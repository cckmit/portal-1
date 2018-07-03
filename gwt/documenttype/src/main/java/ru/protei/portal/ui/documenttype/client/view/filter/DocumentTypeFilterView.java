package ru.protei.portal.ui.documenttype.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.documenttype.client.activity.filter.AbstractDocumentTypeFilterActivity;
import ru.protei.portal.ui.documenttype.client.activity.filter.AbstractDocumentTypeFilterView;
import ru.protei.portal.ui.documenttype.client.widget.DocumentCategoryBtnGroupMulti;

import java.util.Set;

public class DocumentTypeFilterView extends Composite implements AbstractDocumentTypeFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        resetFilter();
    }

    @Override
    public void setActivity(AbstractDocumentTypeFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        name.setValue(null);
        sortField.setValue(En_SortField.name);
        documentCategories.setValue(null);
        sortDir.setValue(false);
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<Set<En_DocumentCategory>> documentCategories() {
        return documentCategories;
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event) {
        if (activity != null) {
            resetFilter();
            if (activity != null) {
                activity.onFilterChanged();
            }
        }
    }

    @UiHandler("name")
    public void onSearchChanged( ValueChangeEvent<String> event) {
        fireChangeTimer();
    }

    @UiHandler("sortDir")
    public void onSortDirChanged(ValueChangeEvent<Boolean> event) {
        fireChangeTimer();
    }

    @UiHandler("sortField")
    public void onSortFieldChanged(ValueChangeEvent<En_SortField> event) {
        fireChangeTimer();
    }

    @UiHandler("documentCategories")
    public void oDocumentCategoriesChanged(ValueChangeEvent<Set<En_DocumentCategory>> event) {
        fireChangeTimer();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    private void fireChangeTimer() {
        timer.cancel();
        timer.schedule(300);
    }

    private final Timer timer = new Timer() {
        @Override
        public void run() {
            if (activity != null) {
                activity.onFilterChanged();
            }
        }
    };

    @UiField
    Button resetBtn;
    @Inject
    @UiField
    Lang lang;
    @UiField
    CleanableSearchBox name;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField(provided = true)
    DocumentCategoryBtnGroupMulti documentCategories;

    @Inject
    FixedPositioner positioner;

    AbstractDocumentTypeFilterActivity activity;

    private static DocumentTypeFilterViewUiBinder outUiBinder = GWT.create(DocumentTypeFilterViewUiBinder.class);
    interface DocumentTypeFilterViewUiBinder extends UiBinder<HTMLPanel, DocumentTypeFilterView> {}
}
