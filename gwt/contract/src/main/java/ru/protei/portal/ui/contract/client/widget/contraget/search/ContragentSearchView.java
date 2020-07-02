package ru.protei.portal.ui.contract.client.widget.contraget.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

public class ContragentSearchView extends Composite implements AbstractContragentSearchView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        contragentINN.setRegexp("^d{50}$");
        contragentKPP.setRegexp("^d{9}$");

        reset();
    }

    @Override
    public void setActivity(AbstractContragentSearchActivity activity){
        this.activity = activity;
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
    public void setSearchSuccessResult(String name) {
        root.add(successContainer);
        contragentName.setValue(name);
    }

    @Override
    public void setSearchFaultResult() {
        root.add(faultContainer);
    }

    @Override
    public void reset() {
        root.remove(successContainer);
        root.remove(faultContainer);
    }

    @UiHandler( "search" )
    public void onSearchClicked ( ClickEvent event ) {
        root.remove(successContainer);
        root.remove(faultContainer);
        activity.onSearchClicked();
    }

    @UiHandler( "create" )
    public void onCreateClicked ( ClickEvent event ) {
        root.remove(successContainer);
        root.remove(faultContainer);
        activity.onCreateClicked();
    }

    @UiField
    ValidableTextBox contragentINN;

    @UiField
    ValidableTextBox contragentKPP;

    @UiField
    Button search;

    @UiField
    HTMLPanel root;

    @UiField
    HTMLPanel successContainer;

    @UiField
    TextBox contragentName;

    @UiField
    HTMLPanel faultContainer;

    @UiField
    Button create;

    AbstractContragentSearchActivity activity;

    private static ContragentSearchViewUiBinder ourUiBinder = GWT.create(ContragentSearchViewUiBinder.class);
    interface ContragentSearchViewUiBinder extends UiBinder<HTMLPanel, ContragentSearchView> {}
}
