package ru.protei.portal.ui.plan.client.view.columns;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;

import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

public class IssueColumn extends ClickColumn<CaseShortView> {

    @Inject
    public IssueColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {}

    @Override
    public void fillColumnValue(Element cell, CaseShortView value) {
        cell.addClassName("plan-issue-column");
        if (styleName != null) {
            cell.addClassName(styleName);
        }
        cell.appendChild(makeContent(value));
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    private Node makeContent(CaseShortView value) {
        DivElement div = Document.get().createDivElement();
        div.appendChild(makePrimaryBlock(value));
        div.appendChild(makeSecondaryBlock(value));
        return div;
    }

    private Node makePrimaryBlock(CaseShortView value) {
        boolean isPrivate = value.isPrivateCase();
        Long number = value.getCaseNumber();
        String name = value.getName();

        DivElement div = Document.get().createDivElement();
        div.appendChild(makeSpan(ImportanceStyleProvider.getImportanceIcon(value.getImportanceCode()), ""));
        div.appendChild(makeSpan("label label-" + CaseStateUtils.makeStyleName(value.getStateName()), value.getStateName()));
        if (isPrivate) div.appendChild(makeSpan("fa fa-fw fa-lock text-danger", ""));
        div.appendChild(makeSpan("font-weight-bold", String.valueOf(number)));
        div.appendChild(makeSpan("word-break-word", name));
        return div;
    }

    private Node makeSecondaryBlock(CaseShortView value) {

        String product = value.getProductName();
        String initiatorCompany = value.getInitiatorCompanyName();
        String initiatorName = isNotEmpty(value.getInitiatorShortName())
                ? "(" + value.getInitiatorShortName() + ")"
                : "";

        DivElement div = Document.get().createDivElement();
        if (isNotEmpty(product)) div.appendChild(makeSpan("font-weight-bold", product));
        if (isNotEmpty(initiatorCompany)) div.appendChild(makeSpan("", initiatorCompany));
        if (isNotEmpty(initiatorName)) div.appendChild(makeSpan("", initiatorName));
        return div;
    }

    private SpanElement makeSpan(String className, String text) {
        SpanElement span = Document.get().createSpanElement();
        span.setClassName(className);
        span.setInnerText(text);
        return span;
    }

    private Lang lang;
    private String styleName;
}
