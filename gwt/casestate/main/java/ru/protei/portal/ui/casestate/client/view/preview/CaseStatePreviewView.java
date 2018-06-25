package ru.protei.portal.ui.casestate.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LegendElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.casestate.client.activity.preview.AbstractCaseStatePreviewActivity;
import ru.protei.portal.ui.casestate.client.activity.preview.AbstractCaseStatePreviewView;
import ru.protei.portal.ui.casestate.client.view.btngroup.UsageInCompaniesBtnGroup;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyMultiSelector;

import java.util.Set;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 * Вид превью роли
 */
public class CaseStatePreviewView extends Composite implements AbstractCaseStatePreviewView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    @Override
    public void setActivity(AbstractCaseStatePreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName( String value ) {
        this.name.setInnerText( value );
    }

    @Override
    public HasValue<String> description() {
        return description;
    }

    @Override
    public HasValue<En_CaseStateUsageInCompanies> usageInCompanies() {
        return usageInCompanies;
    }

    @Override
    public HasVisibility companiesVisibility() {
        return companies;
    }

    @Override
    public void setViewEditable(boolean isEditable) {
        description.setEnabled(isEditable);
        saveButton.setEnabled(isEditable);
        companies.setEnabled(isEditable);
    }

    @Override
    public HasValue<Set<EntityOption>> companies() {
        return companies;
    }

    @UiHandler( "usageInCompanies" )
    public void onUsageInCompaniesChange( ValueChangeEvent<En_CaseStateUsageInCompanies> event ) {
        activity.onUsageInCompaniesChange();
    }

    @UiHandler( "saveButton" )
    public void onSaveClicked( ClickEvent event ) {
        activity.onSaveClicked();
    }

    private static final Logger log = getLogger(CaseStatePreviewView.class.getName());

    @Inject
    @UiField
    Lang lang;
    @UiField
    SpanElement name;
    @UiField
    TextArea description;
    @UiField
    SpanElement usageInCompaniesTxt;
    @UiField
    LegendElement header;
    @UiField
    HTMLPanel preview;
    @Inject
    @UiField( provided = true )
    CompanyMultiSelector companies;

    @Inject
    @UiField(provided = true)
    UsageInCompaniesBtnGroup usageInCompanies;
    @UiField
    Button saveButton;

    @Inject
    FixedPositioner positioner;

    AbstractCaseStatePreviewActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, CaseStatePreviewView > { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}