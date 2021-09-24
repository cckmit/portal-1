package ru.protei.portal.ui.delivery.client.view.card.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.selector.card.state.CardStateFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.card.type.CardTypeFormSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeFormSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.delivery.client.activity.card.meta.AbstractCardMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.card.meta.AbstractCardMetaView;

import java.util.Date;

public class CardMetaView extends Composite implements AbstractCardMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractCardMetaActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<CaseState> state() {
        return state;
    }

    @Override
    public HasValue<EntityOption> type() {
        return type;
    }

    @Override
    public HasValue<String> article() {
        return article;
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public HasValue<Date> testDate() {
        return testDate;
    }

    @Override
    public boolean isTestDateEmpty() {
        return HelperFunc.isEmpty(testDate.getInputValue());
    }

    @Override
    public void setTestDateValid(boolean isValid) {
        testDate.markInputValid(isValid);
    }

    @Override
    public void setAllowChangingState(boolean isAllow) {
        // ???
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
/*        state.setEnsureDebugId(DebugIds.DELIVERY.KIT.MODULE.STATE);
        customerCompany.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.CUSTOMER_COMPANY);
        manager.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.MANAGER);
        hwManager.ensureLabelDebugId(DebugIds.DELIVERY.KIT.MODULE.HW_MANAGER);
        qcManager.ensureLabelDebugId(DebugIds.DELIVERY.KIT.MODULE.QC_MANAGER);
        buildDate.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.BUILD_DATE);
        departureDate.ensureDebugId(DebugIds.DELIVERY.KIT.MODULE.DEPARTURE_DATE);*/
    }

    @UiHandler("state")
    public void onStateChanged(ValueChangeEvent<CaseState> event) {
        if (activity != null) activity.onStateChanged();
    }

    @UiHandler("article")
    public void onArticleChanged(ValueChangeEvent<String> event) {
        if (activity != null) activity.onArticleChanged();
    }

    @UiHandler("manager")
    public void onManagerChanged(ValueChangeEvent<PersonShortView> event) {
        if (activity != null) activity.onManagerChanged();
    }

    @UiHandler("testDate")
    public void onTestDateChanged(ValueChangeEvent<Date> event) {
        if (activity != null) activity.onTestDateChanged();
    }

    @Inject
    @UiField( provided = true )
    CardStateFormSelector state;
    @Inject
    @UiField( provided = true )
    CardTypeFormSelector type;
    @UiField
    ValidableTextBox article;
    @Inject
    @UiField(provided = true)
    EmployeeFormSelector manager;
    @Inject
    @UiField(provided = true)
    SinglePicker testDate;

    private AbstractCardMetaActivity activity;

    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
    interface ViewUiBinder extends UiBinder<HTMLPanel, CardMetaView> {}
}
