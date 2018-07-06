package ru.protei.portal.ui.sitefolder.client.view.platform.filter;

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
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.filter.AbstractSiteFolderFilterActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.filter.AbstractSiteFolderFilterView;

import java.util.Set;

public class SiteFolderFilterView extends Composite implements AbstractSiteFolderFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
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

    @Override
    public void setActivity(AbstractSiteFolderFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        name.setValue(null);
        sortField.setValue(En_SortField.name);
        sortDir.setValue(false);
        companies.setValue(null);
        parameters.setValue(null);
        comment.setValue(null);
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
    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    @Override
    public HasValue<String> parameters() {
        return parameters;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @UiHandler("resetBtn")
    public void resetBtnClick(ClickEvent event) {
        if (activity != null) {
            resetFilter();
            if (activity != null) {
                activity.onFilterChanged();
            }
        }
    }

    @UiHandler("name")
    public void onNameChanged(ValueChangeEvent<String> event) {
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

    @UiHandler("companies")
    public void onCompaniesSelected(ValueChangeEvent<Set<EntityOption>> event) {
        fireChangeTimer();
    }

    @UiHandler("parameters")
    public void onParametersChanged(ValueChangeEvent<String> event) {
        fireChangeTimer();
    }

    @UiHandler("comment")
    public void onCommentKeyUp(KeyUpEvent event) {
        fireChangeTimer();
    }

    private void fireChangeTimer() {
        timer.cancel();
        timer.schedule(200);
    }

    private final Timer timer = new Timer() {
        @Override
        public void run() {
            if (activity != null) {
                activity.onFilterChanged();
            }
        }
    };

    @Inject
    @UiField
    Lang lang;
    @UiField
    Button resetBtn;
    @UiField
    CleanableSearchBox name;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField(provided = true)
    CompanyMultiSelector companies;
    @UiField
    CleanableSearchBox parameters;
    @UiField
    TextArea comment;

    @Inject
    FixedPositioner positioner;

    private AbstractSiteFolderFilterActivity activity;

    interface SiteFolderFilterViewUiBinder extends UiBinder<HTMLPanel, SiteFolderFilterView> {}
    private static SiteFolderFilterViewUiBinder outUiBinder = GWT.create(SiteFolderFilterViewUiBinder.class);
}
