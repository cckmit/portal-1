package ru.protei.portal.ui.contract.client.widget.contractor;

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
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ContractorQuery;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.contract.client.widget.contractor.create.AbstractContractorCreateView;
import ru.protei.portal.ui.contract.client.widget.contractor.search.AbstractContractorSearchActivity;
import ru.protei.portal.ui.contract.client.widget.contractor.search.AbstractContractorSearchView;

import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.WIDE_MODAL;

abstract public class ContractorWidget extends Composite implements HasValue<Contractor>, HasEnabled, HasValidable, Activity {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        name.getElement().setAttribute("placeholder", lang.selectContractContractor());
        root.setTitle(lang.contractContractorOrganizationHint());
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
        if (isValidable) {
            setValid(isValid());
        }
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
    }

    public void setValidation(boolean isValidable){
        this.isValidable = isValidable;
    }

    @Override
    public boolean isValid(){
        return getValue() != null;
    }

    @Override
    public void setValid(boolean isValid) {
        if (isValid) {
            name.removeStyleName(REQUIRED);
        } else {
            name.addStyleName(REQUIRED);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Contractor> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler( "button" )
    public void onButtonClicked ( ClickEvent event ) {
        searchView.reset();
        searchView.setOrganization(organization);
        dialogDetailsSearchView.showPopup();
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setEnsureDebugId( String debugId ) {
        button.ensureDebugId(debugId);
    }

    private void prepareSearchDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(makeSearchDialogActivity());
        dialog.getBodyContainer().add(searchView.asWidget());
        dialog.setHeader(lang.searchContractorTitle());
        dialog.setSaveButtonName(lang.buttonApply());
        dialog.setAdditionalButtonName(lang.buttonCreate());
        dialog.setAdditionalVisible(true);
        dialog.removeButtonVisibility().setVisible(true);
    }

    private void prepareCreateDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(makeCreateDialogActivity());
        dialog.addStyleName(WIDE_MODAL);
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
            @Override
            public void onAdditionalClicked() {
                    createView.reset();
                    createView.setOrganization(organization);
                    dialogDetailsSearchView.hidePopup();
                    dialogDetailsCreateView.showPopup();
            }
            @Override
            public void onRemoveClicked() {
                Contractor contractor = searchView.contractor().getValue();
                if (contractor == null) {
                    fireEvent(new NotifyEvents.Show(lang.contractContractorFindNotChosenError(), NotifyEvents.NotifyType.INFO));
                    return;
                }
                fireEvent(new ConfirmDialogEvents.Show(lang.contractorRemoveConfirmMessage(), removeContractorAction(contractor)));
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

                Contractor contractor = makeContractor();

                controller.createContractor(contractor, new FluentCallback<Contractor>()
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

    private Contractor makeContractor() {
        Contractor contractor = new Contractor();
        contractor.setOrganization(organization);
        contractor.setInn(createView.contractorInn().getValue());
        contractor.setKpp(createView.contractorKpp().getValue());
        contractor.setName(createView.contractorName().getValue());
        contractor.setFullName(createView.contractorFullName().getValue());
        contractor.setCountryRef(createView.contractorCountry().getValue() == null ?
                null : createView.contractorCountry().getValue().getRefKey() );
        return contractor;
    }

    private AbstractContractorSearchActivity makeSearchViewActivity() {
        return () -> {
            ContractorQuery query = makeContractorQuery(searchView);
            if (!searchView.isValid() || !isValidContractorQuery(query)) {
                fireEvent(new NotifyEvents.Show(lang.contractContractorValidationError(), NotifyEvents.NotifyType.ERROR));
                return;
            }
            controller.findContractors(organization, query, new FluentCallback<List<Contractor>>()
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
        };
    }

    private ContractorQuery makeContractorQuery(AbstractContractorSearchView searchView) {
        ContractorQuery query = new ContractorQuery();
        query.setInn(searchView.contractorInn().getValue());
        query.setKpp(searchView.contractorKpp().getValue());
        query.setName(searchView.contractorName().getValue());
        query.setFullName(searchView.contractorFullName().getValue());
        query.setRegistrationCountryKey(searchView.contractorCountry().getValue() != null
                ? searchView.contractorCountry().getValue().getRefKey()
                : null);
        return query;
    }

    private boolean isValidContractorQuery(ContractorQuery query) {
        if (query == null) {
            return false;
        }
        if (isNotEmpty(query.getKpp()) && isEmpty(query.getInn())) {
            return false;
        }
        return isNotEmpty(query.getInn())
                || isNotEmpty(query.getKpp())
                || isNotEmpty(query.getName())
                || isNotEmpty(query.getFullName())
                || isNotEmpty(query.getRegistrationCountryKey());
    }

    private Runnable removeContractorAction(Contractor contractor) {
        return () -> {
            if (contractor == null) {
                return;
            }
            String refKey = contractor.getRefKey();
            controller.removeContractor(organization, refKey, new FluentCallback<Long>()
                    .withSuccess(id -> {
                        fireEvent(new NotifyEvents.Show(lang.contractorRemoved(), NotifyEvents.NotifyType.SUCCESS));
                        String refKeySelected = getValue() != null
                                ? getValue().getRefKey()
                                : null;
                        if (Objects.equals(refKeySelected, refKey)) {
                            setValue(null);
                        }
                        searchView.reset();
                        dialogDetailsSearchView.hidePopup();
                    }));
        };
    }

    @Inject
    AbstractContractorSearchView searchView;

    @Inject
    AbstractContractorCreateView createView;

    @Inject
    AbstractDialogDetailsView dialogDetailsSearchView;

    @Inject
    AbstractDialogDetailsView dialogDetailsCreateView;

    @UiField
    HTMLPanel root;

    @UiField
    TextBox name;

    @UiField
    Button button;

    @UiField
    Lang lang;

    @Inject
    ContractControllerAsync controller;

    private Contractor value;
    private String organization;
    private boolean isValidable;

    interface ContractorWidgetUiBinder extends UiBinder<HTMLPanel, ContractorWidget> {}
    private static ContractorWidgetUiBinder ourUiBinder = GWT.create( ContractorWidgetUiBinder.class );
}
