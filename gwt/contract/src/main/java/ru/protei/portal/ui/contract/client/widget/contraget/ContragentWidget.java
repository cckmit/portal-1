package ru.protei.portal.ui.contract.client.widget.contraget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.ent.ContractorAPI;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.contract.client.widget.contraget.create.AbstractContragentCreateView;
import ru.protei.portal.ui.contract.client.widget.contraget.search.AbstractContragentSearchActivity;
import ru.protei.portal.ui.contract.client.widget.contraget.search.AbstractContragentSearchView;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

abstract public class ContragentWidget extends Composite implements HasValue<Contractor>, HasEnabled, Activity {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        name.getElement().setAttribute("placeholder", lang.selectContractContractor());
        prepareSearchDialog(dialogDetailsSearchView);
        prepareCreateDialog(dialogDetailsCreateView);
        searchView.setActivity(makeSearchViewActivity());
    }

    @Override
    public Contractor getValue() {
        return value;
    }

    @Override
    public void setValue(Contractor value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Contractor value, boolean fireEvents) {
        this.value = value;
        name.setValue(value != null ? value.getName() : null, fireEvents);
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Contractor> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler( "button" )
    public void onButtonClicked ( ClickEvent event ) {
        searchView.reset();
        dialogDetailsSearchView.showPopup();
    }

    private void ensureDebugIds() {
        name.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.CONTRAGENT.NAME);
        button.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.CONTRAGENT.SEARCH_BUTTON);
    }

    public void setEnsureDebugId( String debugId ) {
        button.ensureDebugId(debugId);
    }

    private void prepareSearchDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(makeSearchDialogActivity());
        dialog.getBodyContainer().add(searchView.asWidget());
        dialog.removeButtonVisibility().setVisible(false);
        dialog.setHeader(lang.searchContractorTitle());
        dialog.setSaveButtonName(lang.buttonApply());
    }

    private void prepareCreateDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(makeCreateDialogActivity());
        dialog.getBodyContainer().add(createView.asWidget());
        dialog.removeButtonVisibility().setVisible(false);
        dialog.setHeader(lang.createContractorTitle());
        dialog.setSaveButtonName(lang.buttonCreate());
    }

    private AbstractDialogDetailsActivity makeSearchDialogActivity() {
        return new AbstractDialogDetailsActivity(){
            @Override
            public void onSaveClicked() {
                if (searchView.contractor().getValue() == null) {
                    fireEvent(new NotifyEvents.Show(lang.contractContractorFindNotChosenError(), NotifyEvents.NotifyType.INFO));
                    return;
                }
                setValue(searchView.contractor().getValue());
                dialogDetailsSearchView.hidePopup();
            }
            @Override
            public void onCancelClicked() {
                dialogDetailsSearchView.hidePopup();
            }
        };
    }

    private AbstractDialogDetailsActivity makeCreateDialogActivity() {
        return new AbstractDialogDetailsActivity(){
            @Override
            public void onSaveClicked() {
                if (!createView.isValid()) {
                    fireEvent(new NotifyEvents.Show(lang.contractContractorValidationError(), NotifyEvents.NotifyType.ERROR));
                    return;
                }

                ContractorAPI contractorAPI = makeContractorAPIDTO();

                controller.createContractor(contractorAPI, new FluentCallback<Contractor>()
                        .withError(t -> {
                            fireEvent(new NotifyEvents.Show(lang.contractContractorSaveError(), NotifyEvents.NotifyType.ERROR));
                        })
                        .withSuccess(value -> {
                            setValue(value);
                            dialogDetailsCreateView.hidePopup();
                        }));
            }

            @Override
            public void onCancelClicked() {
                dialogDetailsCreateView.hidePopup();
            }
        };
    }

    private ContractorAPI makeContractorAPIDTO() {
        ContractorAPI contractorAPI = new ContractorAPI();
        contractorAPI.setInn(createView.contragentINN().getValue());
        contractorAPI.setKpp(createView.contragentKPP().getValue());
        contractorAPI.setName(createView.contragentName().getValue());
        contractorAPI.setFullname(createView.contragentFullname().getValue());
        contractorAPI.setCountry(createView.contragentCountry().getValue());
        contractorAPI.setResident(createView.contragentResident().getValue());
        return contractorAPI;
    }

    private AbstractContragentSearchActivity makeSearchViewActivity() {
        return new AbstractContragentSearchActivity() {
            @Override
            public void onSearchClicked() {
                if (!searchView.isValid()) {
                    fireEvent(new NotifyEvents.Show(lang.contractContractorValidationError(), NotifyEvents.NotifyType.ERROR));
                    return;
                }
                controller.findContractors(searchView.contragentINN().getValue(), searchView.contragentKPP().getValue(), new FluentCallback<List<Contractor>>()
                        .withError(t -> {
                            fireEvent(new NotifyEvents.Show(lang.contractContractorFindError(), NotifyEvents.NotifyType.ERROR));
                        })
                        .withSuccess(value -> {
                            if (isEmpty(value)) {
                                fireEvent(new NotifyEvents.Show(lang.contractContractorNotFound(), NotifyEvents.NotifyType.INFO));
                                return;
                            }
                            searchView.setSearchResult(value);
                        }));
            }

            @Override
            public void onCreateClicked() {
                createView.reset();
                dialogDetailsSearchView.hidePopup();
                dialogDetailsCreateView.showPopup();
            }
        };
    }

    @Inject
    AbstractContragentSearchView searchView;

    @Inject
    AbstractContragentCreateView createView;

    @Inject
    AbstractDialogDetailsView dialogDetailsSearchView;

    @Inject
    AbstractDialogDetailsView dialogDetailsCreateView;

    @UiField
    TextBox name;

    @UiField
    Button button;

    @UiField
    Lang lang;

    @Inject
    ContractControllerAsync controller;

    private Contractor value;

    interface ContragentWidgetUiBinder extends UiBinder<HTMLPanel, ContragentWidget> {}
    private static ContragentWidgetUiBinder ourUiBinder = GWT.create( ContragentWidgetUiBinder.class );
}
