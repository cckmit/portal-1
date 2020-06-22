package ru.protei.portal.ui.contract.client.widget.contractspecification.previewitem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ContractSpecificationPreviewItem
        extends Composite
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setTestAttributes();
    }

    public void setValue( ContractSpecification value ) {
         if ( value == null ) {
            value = new ContractSpecification();
        }
        clause.setInnerText( value.getClause() );
        text.setInnerHTML( value.getText() == null? value.getText() : value.getText().replaceAll("\n", "<br>")  );
    }

    private void setTestAttributes() {
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.ITEM);
        clause.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.CLAUSE_INPUT);
        text.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.TEXT_INPUT);
    }

    @UiField
    SpanElement clause;
    @UiField
    SpanElement text;

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;

    interface ContractSpecificationUiBinder extends UiBinder< HTMLPanel, ContractSpecificationPreviewItem> {}
    private static ContractSpecificationUiBinder ourUiBinder = GWT.create( ContractSpecificationUiBinder.class );
}