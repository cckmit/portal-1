package ru.protei.portal.ui.issuereport.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.columns.StaticColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.*;

import java.util.stream.Collectors;

public class FilterColumn extends StaticColumn<Report> {

    @Inject
    public FilterColumn(Lang lang, En_SortFieldLang sortFieldLang, En_SortDirLang sortDirLang,
                        En_CaseImportanceLang caseImportanceLang, En_CaseStateLang caseStateLang ) {
        this.lang = lang;
        this.sortFieldLang = sortFieldLang;
        this.sortDirLang = sortDirLang;
        this.caseImportanceLang = caseImportanceLang;
        this.caseStateLang = caseStateLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("filter");
        columnHeader.setInnerText(lang.issueReportsFilter());
    }

    @Override
    public void fillColumnValue(Element cell, Report value) {
        cell.addClassName("filter");

        Element divElement = DOM.createDiv();

        CaseQuery caseQuery = value == null ? null : value.getCaseQuery();

        if (value == null || caseQuery == null) {
            cell.appendChild(divElement);
            return;
        }

        // search string
        if (HelperFunc.isNotEmpty(caseQuery.getSearchString())) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.search() + ": " + caseQuery.getSearchString());
            divElement.appendChild(managerElement);
        }

        // date from to
        if (caseQuery.getFrom() != null || caseQuery.getTo() != null) {
            Element managerElement = DOM.createElement("p");
            StringBuilder sb = new StringBuilder();
            sb.append(lang.created()).append(": ");
            if (caseQuery.getFrom() != null) {
                sb.append(lang.from().toLowerCase()).append(" ").append(DateFormatter.formatDateTime(caseQuery.getFrom())).append(" ");
            }
            if (caseQuery.getTo() != null) {
                sb.append(lang.to().toLowerCase()).append(" ").append(DateFormatter.formatDateTime(caseQuery.getTo())).append(" ");
            }
            managerElement.setInnerText(sb.toString());
            divElement.appendChild(managerElement);
        }

        // sorting
        if (caseQuery.getSortField() != null) {
            Element managerElement = DOM.createElement("p");
            StringBuilder sb = new StringBuilder();
            sb.append(lang.sortBy()).append(": ").append(sortFieldLang.getName(caseQuery.getSortField()).toLowerCase());
            if (caseQuery.getSortDir() != null) {
                sb.append(" ").append(sortDirLang.getName(caseQuery.getSortDir()).toLowerCase());
            }
            managerElement.setInnerText(sb.toString());
            divElement.appendChild(managerElement);
        }

        // importance
        if (caseQuery.getImportanceIds() != null && !caseQuery.getImportanceIds().isEmpty()) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.issueImportance() + ": " +
                    caseQuery.getImportanceIds()
                            .stream()
                            .map(En_ImportanceLevel::getById)
                            .map(caseImportanceLang::getImportanceName)
                            .collect(Collectors.joining(", "))
            );
            divElement.appendChild(managerElement);
        }

        // states
        if (caseQuery.getStateIds() != null && !caseQuery.getStateIds().isEmpty()) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.issueState() + ": " +
                    caseQuery.getStateIds()
                            .stream()
                            .map(id -> En_CaseState.getById(Long.valueOf(id)))
                            .map(caseStateLang::getStateName)
                            .collect(Collectors.joining(", "))
            );
            divElement.appendChild(managerElement);
        }

        // companies
        if (caseQuery.getCompanyIds() != null && !caseQuery.getCompanyIds().isEmpty()) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.issueCompany() + ": " + caseQuery.getCompanyIds().size() + " " + lang.selected().toLowerCase());
            divElement.appendChild(managerElement);
        }

        // products
        if (caseQuery.getProductIds() != null && !caseQuery.getProductIds().isEmpty()) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.issueProduct() + ": " + caseQuery.getProductIds().size() + " " + lang.selected().toLowerCase());
            divElement.appendChild(managerElement);
        }

        // managers
        if (caseQuery.getManagerIds() != null && !caseQuery.getManagerIds().isEmpty()) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.issueManager() + ": " + caseQuery.getManagerIds().size() + " " + lang.selected().toLowerCase());
            divElement.appendChild(managerElement);
        }

        cell.appendChild(divElement);
    }

    private Lang lang;
    private En_SortFieldLang sortFieldLang;
    private En_SortDirLang sortDirLang;
    private En_CaseImportanceLang caseImportanceLang;
    private En_CaseStateLang caseStateLang;
}
