package ru.protei.portal.ui.casestate.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LegendElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.casestate.client.activity.preview.AbstractCaseStatePreviewActivity;
import ru.protei.portal.ui.casestate.client.activity.preview.AbstractCaseStatePreviewView;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 * Вид превью роли
 */
public class CaseStatePreviewView extends Composite implements AbstractCaseStatePreviewView {

    public CaseStatePreviewView() {
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
    public void setHeader( String value ) {
        this.header.setInnerText( value );
    }

    @Override
    public void setName( String value ) {
        this.name.setInnerText( value );
    }

    @Override
    public void setDescription( String value ) {
        this.description.setInnerText( value );
    }

    @Override
    public void setCompanies(List<Company> companies) {
        log.warning("setCompanies(): Not implemented.");//NotImplemented

    }

    @Override
    public void setUsageInCompanies(String stateName) {
        log.warning("setUsageInCompanies(): Not implemented.");//NotImplemented

    }

    private static final Logger log = getLogger(CaseStatePreviewView.class.getName());

    @Inject
    @UiField
    Lang lang;
    @UiField
    SpanElement name;
    @UiField
    SpanElement description;
    @UiField
    LegendElement header;
    @UiField
    HTMLPanel preview;

    @Inject
    FixedPositioner positioner;

    AbstractCaseStatePreviewActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, CaseStatePreviewView > { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}