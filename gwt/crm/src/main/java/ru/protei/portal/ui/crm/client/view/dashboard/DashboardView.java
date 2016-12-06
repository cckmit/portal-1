package ru.protei.portal.ui.crm.client.view.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.crm.client.activity.dashboard.AbstractDashboardActivity;
import ru.protei.portal.ui.crm.client.activity.dashboard.AbstractDashboardView;
import ru.protei.portal.ui.crm.client.widget.importance.btngroup.CustomImportanceBtnGroup;

import java.util.Set;

/**
 * Created by bondarenko on 01.12.16.
 */
public class DashboardView extends Composite implements AbstractDashboardView{

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));

        CustomImportanceBtnGroup[] selectors = new CustomImportanceBtnGroup[]{
                activeRecordsImportance,
                newRecordsImportance,
                inactiveRecordsImportance
        };
        for(CustomImportanceBtnGroup selector: selectors)
            selector.init(
                    "importance importance-lg",
                    "dashboard-importance-filter-btn",
                    false,
                    null
            );
    }

    @Override
    public void setActivity(AbstractDashboardActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getActiveRecordsContainer() {
        return activeRecordsContainer;
    }

    @Override
    public HasWidgets getNewRecordsContainer() {
        return newRecordsContainer;
    }

    @Override
    public HasWidgets getInactiveRecordsContainer() {
        return inactiveRecordsContainer;
    }

    @Override
    public void setActiveRecordsCount(long count) {
        activeRecordsCount.setInnerText(String.valueOf(count));
    }

    @Override
    public void setNewRecordsCount(long count) {
        newRecordsCount.setInnerText(String.valueOf(count));
    }

    @Override
    public void setInactiveRecordsCount(long count) {
        inactiveRecordsCount.setInnerText(String.valueOf(count));
    }

    @Override
    public HasValue<Set<En_ImportanceLevel>> getActiveRecordsImportance() {
        return activeRecordsImportance;
    }

    @Override
    public HasValue<Set<En_ImportanceLevel>> getNewRecordsImportance() {
        return newRecordsImportance;
    }

    @Override
    public HasValue<Set<En_ImportanceLevel>> getInactiveRecordsImportance() {
        return inactiveRecordsImportance;
    }

    @Override
    public void showInactiveRecordsLoader(boolean isShow){
        if(isShow)
            inactiveRecordsLoader.addClassName("active");
        else
            inactiveRecordsLoader.removeClassName("active");
    }

    @UiHandler( "activeRecordsImportance" )
    public void onActiveRecordsImportanceSelected( ValueChangeEvent<Set<En_ImportanceLevel>> event ) {
        activity.updateActiveRecordsImportance(event.getValue());
    }

    @UiHandler( "newRecordsImportance" )
    public void onNewRecordsImportanceSelected( ValueChangeEvent<Set<En_ImportanceLevel>> event ) {
        activity.updateNewRecordsImportance(event.getValue());
    }

    @UiHandler( "inactiveRecordsImportance" )
    public void onInactiveRecordsImportanceSelected( ValueChangeEvent<Set<En_ImportanceLevel>> event ) {
        activity.updateInactiveRecordsImportance(event.getValue());
    }


    @UiField
    HTMLPanel activeRecordsContainer;
    @UiField
    HTMLPanel newRecordsContainer;
    @UiField
    HTMLPanel inactiveRecordsContainer;
    @UiField
    SpanElement activeRecordsCount;
    @UiField
    SpanElement newRecordsCount;
    @UiField
    SpanElement inactiveRecordsCount;

    @Inject
    @UiField( provided = true )
    CustomImportanceBtnGroup activeRecordsImportance;
    @Inject
    @UiField( provided = true )
    CustomImportanceBtnGroup newRecordsImportance;
    @Inject
    @UiField( provided = true )
    CustomImportanceBtnGroup inactiveRecordsImportance;
    @UiField
    DivElement inactiveRecordsLoader;

    private AbstractDashboardActivity activity;

    interface DashboardViewUiBinder extends UiBinder<HTMLPanel, DashboardView> {}
    private static DashboardViewUiBinder ourUiBinder = GWT.create(DashboardViewUiBinder.class);
}