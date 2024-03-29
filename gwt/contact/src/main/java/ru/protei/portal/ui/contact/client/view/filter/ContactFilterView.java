package ru.protei.portal.ui.contact.client.view.filter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.contact.client.activity.filter.AbstractContactFilterActivity;
import ru.protei.portal.ui.contact.client.activity.filter.AbstractContactFilterView;


/**
 * Представление фильтра контактов
 */
public class ContactFilterView  extends Composite implements AbstractContactFilterView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        company.setDefaultValue( lang.selectContactCompany() );
        sortField.setType( ModuleType.CONTACT );
        ensureDebugIds();
    }

    @Override
    public void setActivity( AbstractContactFilterActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<Boolean> showFired() {
        return showFired;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public void resetFilter() {
        company.setValue( null );
        showFired.setValue( false );
        sortField.setValue( En_SortField.person_full_name );
        sortDir.setValue( true );
        search.setValue( "" );
    }

    @UiHandler( "resetBtn" )
    public void onResetClicked ( ClickEvent event ) {
        if ( activity != null ) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler( "company" )
    public void onCompanySelected( ValueChangeEvent<EntityOption> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "showFired" )
    public void onShowFireClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent< En_SortField > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("sortDir")
    public void onSortDirClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onSearchChanged( ValueChangeEvent<String> event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    private void ensureDebugIds() {
        if ( !DebugInfo.isDebugIdEnabled() ) {
            return;
        }
        search.setDebugIdTextBox(DebugIds.FILTER.SEARCH_INPUT);
        sortFieldLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.FILTER.LABEL.SORT_FIELD);
        sortField.setEnsureDebugId(DebugIds.FILTER.SORT_FIELD_SELECTOR);
        sortDir.ensureDebugId(DebugIds.FILTER.SORT_DIR_BUTTON);
        company.setEnsureDebugIdLabel(DebugIds.FILTER.LABEL.COMPANY);
        company.setEnsureDebugId(DebugIds.FILTER.COMPANY_SELECTOR);
        showFired.ensureDebugId(DebugIds.FILTER.SHOW_FIRED);
        resetBtn.ensureDebugId(DebugIds.FILTER.RESET_BUTTON);
    }

    @Inject
    @UiField( provided = true )
    CompanySelector company;

    @UiField
    CheckBox showFired;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    CleanableSearchBox search;

    @UiField
    Button resetBtn;

    @Inject
    @UiField
    Lang lang;
    @UiField
    LabelElement sortFieldLabel;


    AbstractContactFilterActivity activity;

    private static ContactFilterView.ContactFilterViewUiBinder ourUiBinder = GWT.create( ContactFilterView.ContactFilterViewUiBinder.class );
    interface ContactFilterViewUiBinder extends UiBinder<HTMLPanel, ContactFilterView > {}

}
