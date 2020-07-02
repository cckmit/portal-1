package ru.protei.portal.ui.contract.client.widget.contraget.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.switcher.Switcher;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

public class ContragentCreateView extends Composite implements AbstractContragentCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        contragentINN.setRegexp("^d{50}$");
        contragentKPP.setRegexp("^d{9}$");
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
    public HasValue<String> contragentINN() {
        return contragentINN;
    }

    @Override
    public HasValue<String> contragentKPP() {
        return contragentKPP;
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
        contragentName.setValue(null);
        contragentFullname.setValue(null);
        contragentINN.setValue(null);
        contragentKPP.setValue(null);
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

    @UiField
    ValidableTextBox contragentName;

    @UiField
    ValidableTextBox contragentFullname;

    @UiField
    ValidableTextBox contragentINN;

    @UiField
    ValidableTextBox contragentKPP;

    @UiField
    ValidableTextBox contragentCountry;

    @UiField
    Switcher contragentResident;

    @UiField
    SpanElement error;

    @UiField
    HTMLPanel root;

    private static ContragentSearchViewUiBinder ourUiBinder = GWT.create(ContragentSearchViewUiBinder.class);
    interface ContragentSearchViewUiBinder extends UiBinder<HTMLPanel, ContragentCreateView> {}
}
