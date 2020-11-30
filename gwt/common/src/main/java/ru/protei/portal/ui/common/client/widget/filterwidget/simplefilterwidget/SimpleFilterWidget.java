package ru.protei.portal.ui.common.client.widget.filterwidget.simplefilterwidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.filterwidget.AbstractFilterWidget;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterParamView;

public abstract class SimpleFilterWidget<Q extends FilterQuery> extends Composite
        implements AbstractFilterWidget<Q> {

    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void resetFilter() {
        filterParamView.resetFilter();
    }

    @Override
    public void setOnFilterChangeCallback(Runnable callback) {
        filterParamView.setOnFilterChangeCallback(callback);
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event) {
        resetFilter();
    }

    private void ensureDebugIds() {
        resetBtn.ensureDebugId(DebugIds.FILTER.RESET_BUTTON);
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel root;

    @UiField(provided = true)
    protected FilterParamView<Q> filterParamView;

    @UiField
    Button resetBtn;
    @UiField
    DivElement footer;

    private static SimpleFilterWidgetUiBinder ourUiBinder = GWT.create(SimpleFilterWidgetUiBinder.class);
    interface SimpleFilterWidgetUiBinder extends UiBinder<HTMLPanel, SimpleFilterWidget> {}
}
