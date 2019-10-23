package ru.protei.portal.ui.sitefolder.client.view.server.filter;

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
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.sitefolder.client.activity.server.filter.AbstractServerFilterActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.filter.AbstractServerFilterView;
import ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector.PlatformMultiSelector;

import java.util.Set;

public class ServerFilterView extends Composite implements AbstractServerFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractServerFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        name.setValue(null);
        companies.setValue(null);
        platforms.setValue(null);
        sortField.setValue(En_SortField.name);
        sortDir.setValue(false);
        ip.setValue(null);
        parameters.setValue(null);
        comment.setValue(null);
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    @Override
    public HasValue<Set<PlatformOption>> platforms() {
        return platforms;
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
    public HasValue<String> ip() {
        return ip;
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
        resetFilter();
        if (activity != null) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("name")
    public void onNameChanged(ValueChangeEvent<String> event) {
        fireChangeTimer();
    }

    @UiHandler("companies")
    public void onCompaniesSelected(ValueChangeEvent<Set<EntityOption>> event) {
        fireChangeTimer();
    }

    @UiHandler("platforms")
    public void onPlatformsSelected(ValueChangeEvent<Set<PlatformOption>> event) {
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

    @UiHandler("ip")
    public void onIpChanged(ValueChangeEvent<String> event) {
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
    CompanyMultiSelector companies;
    @Inject
    @UiField(provided = true)
    PlatformMultiSelector platforms;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @UiField
    CleanableSearchBox ip;
    @UiField
    CleanableSearchBox parameters;
    @UiField
    TextArea comment;

    private AbstractServerFilterActivity activity;

    interface SiteFolderServerFilterViewUiBinder extends UiBinder<HTMLPanel, ServerFilterView> {}
    private static SiteFolderServerFilterViewUiBinder outUiBinder = GWT.create(SiteFolderServerFilterViewUiBinder.class);
}
