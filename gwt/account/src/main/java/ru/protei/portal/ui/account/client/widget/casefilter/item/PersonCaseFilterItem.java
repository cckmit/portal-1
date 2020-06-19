package ru.protei.portal.ui.account.client.widget.casefilter.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
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
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;


/**
 * Один элемент списка
 */
public class PersonCaseFilterItem
        extends Composite
        implements TakesValue<CaseFilterShortView>, HasEnabled
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
        if (value == null) {
            oldFilterId = null;
        } else {
            oldFilterId = value.getId();
        }
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

    @UiHandler( "filter" )
    public void onFilterChanged( ValueChangeEvent<CaseFilterShortView> event) {
        Long newFilterId = filter.getValue() == null ? null : filter.getValue().getId();

        if (oldFilterId == null && newFilterId == null) {
            return;
        }

        if (oldFilterId == null) {
            callbacks.add(newFilterId);
        } else {
            if (newFilterId == null) {
                callbacks.remove(oldFilterId);
            } else {
                callbacks.change(oldFilterId, newFilterId);
            }
        }

        oldFilterId = newFilterId;
    }

    public void setCallback(PersonCaseFilterCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Inject
    @UiField(provided = true)
    IssueFilterSelector filter;
    @UiField
    Lang lang;

    private PersonCaseFilterCallbacks callbacks;
    private Long oldFilterId = null;

    interface PersonCaseFilterItemUiBinder extends UiBinder< HTMLPanel, PersonCaseFilterItem> {}
    private static PersonCaseFilterItemUiBinder ourUiBinder = GWT.create( PersonCaseFilterItemUiBinder.class );
}