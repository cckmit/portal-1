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
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contract.client.widget.contraget.search.AbstractContragentSearchActivity;
import ru.protei.portal.ui.contract.client.widget.contraget.search.AbstractContragentSearchView;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ContragentWidget extends Composite implements HasValue<String> {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        name.getElement().setAttribute("placeholder", lang.selectContractContragent());
        searchView.setActivity(new AbstractContragentSearchActivity() {
            @Override
            public void onSearchClicked() {
//                searchView.setSearchSuccessResult("ПАО Никита крут");
                searchView.setSearchFaultResult();
            }

            @Override
            public void onCreateClicked() {

            }
        });
    }

    @Override
    public String getValue() {
        return name.getValue();
    }

    @Override
    public void setValue(String value) {
        name.setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        name.setValue(value, fireEvents);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void ensureDebugIds() {
        name.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.CONTRAGENT.NAME);
        button.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.CONTRAGENT.SEARCH_BUTTON);
    }

    @UiHandler( "button" )
    public void onButtonClicked ( ClickEvent event ) {
        searchView.reset();
        dialogDetailsView.setActivity(searchDialogActivity);
        dialogDetailsView.getBodyContainer().add(searchView.asWidget());
        dialogDetailsView.removeButtonVisibility().setVisible(false);
        dialogDetailsView.setHeader(lang.searchContragentTitle());
        dialogDetailsView.showPopup();
    }


    @Inject
    AbstractContragentSearchView searchView;

    @Inject
    AbstractDialogDetailsView dialogDetailsView;

    @UiField
    TextBox name;

    @UiField
    Button button;

    @UiField
    Lang lang;

    AbstractDialogDetailsActivity searchDialogActivity = new AbstractDialogDetailsActivity(){
        @Override
        public void onSaveClicked() {
            name.setValue(searchView.contragentName().getValue());
            dialogDetailsView.hidePopup();
        }

        @Override
        public void onCancelClicked() {
            dialogDetailsView.hidePopup();
        }
    };

    interface ContragentWidgetUiBinder extends UiBinder<HTMLPanel, ContragentWidget> {}
    private static ContragentWidgetUiBinder ourUiBinder = GWT.create( ContragentWidgetUiBinder.class );
}
