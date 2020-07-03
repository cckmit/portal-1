package ru.protei.portal.ui.contract.client.widget.contraget.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.ui.common.client.widget.selector.contractorcountry.ContractorCountrySelector;
import ru.protei.portal.ui.common.client.widget.switcher.Switcher;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

public class ContragentCreateView extends Composite implements AbstractContragentCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        contragentINN.setRegexp("^(\\d{10}|\\d{12})$");
        contragentKPP.setRegexp("^\\d{9}$");
        contragentName.setRegexp("^.{1,100}$");
        contragentFullname.setRegexp("^.{1,250}$");
    }

    @Override
    public HasValue<String> contragentINN() {
        return contragentINN;
    }

    @Override
    public HasValue<String> contragentKPP() {
        return contragentKPP;
    }

    @Override
    public HasValue<String> contragentName() {
        return contragentName;
    }

    @Override
    public HasValue<String> contragentFullname() {
        return contragentFullname;
    }

    @Override
    public HasValue<String> contragentCountry() {
        return contragentCountry;
    }

    @Override
    public HasValue<Boolean> contragentResident() {
        return contragentResident;
    }

    @Override
    public void reset() {
        contragentINN.setValue(null);
        contragentKPP.setValue(null);
        contragentName.setValue(null);
        contragentFullname.setValue(null);
        contragentCountry.setValue(null);
        contragentResident.setValue(false);
    }

    @Override
    public void setError(String value) {
        if (value != null) {
            error.removeClassName("hide");
            error.setInnerText(value);
            return;
        }

        error.addClassName("hide");
        error.setInnerText(null);
    }

    @Override
    public void setValid(boolean isValid) {
        // do nothing
    }

    @Override
    public boolean isValid() {
        return contragentINN.isValid() &
                contragentKPP.isValid() &
                contragentName.isValid() &
                contragentFullname.isValid() &&
                contragentCountry.getValue() != null &&
                isValidInn(contragentINN);
    }

    private boolean isValidInn(ValidableTextBox inn) {
        boolean isValid = ContractorUtils.checkInn(inn.getValue());
        inn.setValid(isValid);
        return isValid;
    }

    @UiHandler( "contragentINN" )
    public void onChangeClause(KeyUpEvent event) {
        if (contragentINN.isValid()) {
            contragentINN.setValid( ContractorUtils.checkInn(contragentINN.getValue()));
        }
    }

    @UiField
    ValidableTextBox contragentINN;

    @UiField
    ValidableTextBox contragentKPP;

    @UiField
    ValidableTextBox contragentName;

    @UiField
    ValidableTextBox contragentFullname;

    @Inject
    @UiField(provided = true)
    ContractorCountrySelector contragentCountry;

    @UiField
    Switcher contragentResident;

    @UiField
    SpanElement error;

    @UiField
    HTMLPanel root;

    private static ContragentSearchViewUiBinder ourUiBinder = GWT.create(ContragentSearchViewUiBinder.class);
    interface ContragentSearchViewUiBinder extends UiBinder<HTMLPanel, ContragentCreateView> {}
}
