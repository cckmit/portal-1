package ru.protei.portal.ui.account.client.widget.casefilter.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;


/**
 * Один элемент списка
 */
public class CaseFilterItem
        extends Composite
        implements TakesValue<CaseFilterShortView>,
        HasCloseHandlers<CaseFilterItem>,
        HasAddHandlers, HasEnabled
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        filter.updateFilterType(En_CaseFilterType.CASE_OBJECTS);
    }

    @Override
    public CaseFilterShortView getValue() {
        return filter.getValue();
    }

    @Override
    public void setValue( CaseFilterShortView value ) {
        filter.setValue(value);
    }

    @Override
    public boolean isEnabled() {
        return filter.isEnabled();
    }

    @Override
    public void setEnabled(boolean b) {
        filter.setEnabled(b);
    }

    @Override
    public HandlerRegistration addCloseHandler( CloseHandler<CaseFilterItem> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @Override
    public HandlerRegistration addAddHandler( AddHandler handler ) {
        return addHandler( handler, AddEvent.getType() );
    }

    @UiHandler( "filter" )
    public void onFilterChanged( ValueChangeEvent<CaseFilterShortView> event) {
        if ( filter.getValue() == null ) {
            CloseEvent.fire( this, this );
        } else {
            AddEvent.fire( this );
        }
    }

    @Inject
    @UiField(provided = true)
    IssueFilterSelector filter;
    @UiField
    Lang lang;

    interface PersonCaseFilterItemUiBinder extends UiBinder< HTMLPanel, CaseFilterItem> {}
    private static PersonCaseFilterItemUiBinder ourUiBinder = GWT.create( PersonCaseFilterItemUiBinder.class );
}