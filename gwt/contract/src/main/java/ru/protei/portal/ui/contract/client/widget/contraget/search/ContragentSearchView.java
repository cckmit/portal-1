package ru.protei.portal.ui.contract.client.widget.contraget.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.util.ContractorUtils;
import ru.protei.portal.ui.common.client.widget.selector.contractor.contractor.ContractorSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.*;

public class ContragentSearchView extends Composite implements AbstractContragentSearchView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        contragentINN.setRegexp(CONTRACTOR_INN);
        contragentKPP.setRegexp(CONTRACTOR_KPP);
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
    public HasValue<Contractor> contractor() {
        return contractor;
    }

    @Override
    public void setSearchResult(List<Contractor> result) {
        contractor.fill(result);
        if (!isEmpty(result)) {
            contractor.setValue(result.get(0));
        }
    }

    @Override
    public void reset() {
        contragentINN.setValue(null);
        contragentKPP.setValue(null);
        contractor.setValue(null);
        contractor.fill(new ArrayList<>());
    }

    @Override
    public void setValid(boolean isValid) {
        // do nothing
    }

    @Override
    public boolean isValid() {
        return contragentINN.isValid() &
                contragentKPP.isValid() &
                isValidInn(contragentINN);
    }

    private boolean isValidInn(ValidableTextBox inn) {
        boolean isValid = ContractorUtils.checkInn(inn.getValue());
        inn.setValid(isValid);
        return isValid;
    }

    @UiHandler( "search" )
    public void onSearchClicked ( ClickEvent event ) {
        activity.onSearchClicked();
    }

    @UiHandler( "create" )
    public void onCreateClicked ( ClickEvent event ) {
        activity.onCreateClicked();
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
    Button search;

    @UiField
    HTMLPanel root;

    @UiField
    Button create;

    @Inject
    @UiField(provided = true)
    ContractorSelector contractor;

    AbstractContragentSearchActivity activity;

    private static ContragentSearchViewUiBinder ourUiBinder = GWT.create(ContragentSearchViewUiBinder.class);
    interface ContragentSearchViewUiBinder extends UiBinder<HTMLPanel, ContragentSearchView> {}
}
