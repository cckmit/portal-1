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

import static com.google.gwt.safehtml.shared.SimpleHtmlSanitizer.sanitizeHtml;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.trim;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ContractSpecificationPreviewItem
        extends Composite
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setTestAttributes();
    }

    public void setValue(ContractSpecification value) {
        if (value == null) {
            value = new ContractSpecification();
        }
        root.addStyleName("nest" + value.getClauseNesting());
        clause.setInnerText(value.getClause());
        text.setInnerSafeHtml(value.getText() == null ? sanitizeHtml("") : sanitizeHtml(value.getText().replaceAll("\n", "<br>")));
        quantityAndCost.setInnerText(makeCostInfo(value));
    }

    private String makeCostInfo(ContractSpecification specification) {
        String quantity = specification.getQuantity() != null
                ? specification.getQuantity().toString() + " " + lang.amountShort() + ". -"
                : "";
        String cost = specification.getCost() != null
                ? specification.getCost().toString()
                : "";
        String currency = specification.getCurrency() != null
                ? specification.getCurrency().getCode()
                : "";
        String value = trim(quantity + " " + cost + " " + currency);
        if (isNotEmpty(value)) {
            value = "(" + value + ")";
        }
        return value;
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
    SpanElement quantityAndCost;

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;

    interface ContractSpecificationUiBinder extends UiBinder< HTMLPanel, ContractSpecificationPreviewItem> {}
    private static ContractSpecificationUiBinder ourUiBinder = GWT.create( ContractSpecificationUiBinder.class );
}