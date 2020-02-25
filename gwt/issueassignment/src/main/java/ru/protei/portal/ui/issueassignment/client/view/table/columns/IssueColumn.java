package ru.protei.portal.ui.issueassignment.client.view.table.columns;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

public class IssueColumn extends ClickColumn<CaseShortView> {

    @Inject
    public IssueColumn(Lang lang, En_CaseStateLang caseStateLang) {
        this.lang = lang;
        this.caseStateLang = caseStateLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {}

    @Override
    public void fillColumnValue(Element cell, CaseShortView value) {
        cell.appendChild(makeContent(value));
    }

    private Node makeContent(CaseShortView value) {
        DivElement div = Document.get().createDivElement();
        div.appendChild(makePrimaryBlock(value));
        div.appendChild(makeSecondaryBlock(value));
        return div;
    }

    private Node makePrimaryBlock(CaseShortView value) {

        En_ImportanceLevel importance = En_ImportanceLevel.getById(value.getImpLevel());
        En_CaseState state = En_CaseState.getById(value.getStateId());
        boolean isPrivate = value.isPrivateCase();
        Long number = value.getCaseNumber();
        String name = value.getName();

        DivElement div = Document.get().createDivElement();
        div.appendChild(makeSpan(ImportanceStyleProvider.getImportanceIcon(importance), ""));
        div.appendChild(makeSpan("label label-" + state.toString().toLowerCase(), caseStateLang.getStateName(state)));
        if (isPrivate) div.appendChild(makeSpan("fa fa-fw fa-lock text-danger", ""));
        div.appendChild(makeSpan("font-weight-bold", String.valueOf(number)));
        div.appendChild(makeSpan("", name));
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
        span.setInnerHTML(text);
        return span;
    }

    private Lang lang;
    private En_CaseStateLang caseStateLang;
}
