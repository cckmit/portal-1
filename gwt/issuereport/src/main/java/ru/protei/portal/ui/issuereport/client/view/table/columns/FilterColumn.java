package ru.protei.portal.ui.issuereport.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.columns.StaticColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.*;

import java.util.Collection;
import java.util.stream.Collectors;

public class FilterColumn extends StaticColumn<Report> {

    @Inject
    public FilterColumn(Lang lang, En_SortFieldLang sortFieldLang, En_SortDirLang sortDirLang,
                        En_CaseImportanceLang caseImportanceLang, En_RegionStateLang regionStateLang) {
        this.lang = lang;
        this.sortFieldLang = sortFieldLang;
        this.sortDirLang = sortDirLang;
        this.caseImportanceLang = caseImportanceLang;
        this.regionStateLang = regionStateLang;
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

        if (value == null) {
            cell.appendChild(divElement);
            return;
        }

        if (value.getCaseQuery() != null) {
            appendCaseQueryInfo(divElement, value.getCaseQuery(), value.getReportType());
        }

        cell.appendChild(divElement);
    }

    private void appendCaseQueryInfo(Element element, CaseQuery caseQuery, En_ReportType en_reportType) {

        // search string
        if (StringUtils.isNotBlank(caseQuery.getSearchString())) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.search() + ": " + caseQuery.getSearchString());
            element.appendChild(managerElement);
        }

        // date CreatedFrom CreatedTo
        if (caseQuery.getCreatedFrom() != null || caseQuery.getCreatedTo() != null) {
            Element managerElement = DOM.createElement("p");
            StringBuilder sb = new StringBuilder();
            sb.append(lang.created()).append(": ");
            if (caseQuery.getCreatedFrom() != null) {
                sb.append(lang.from().toLowerCase()).append(" ").append(DateFormatter.formatDateTime(caseQuery.getCreatedFrom())).append(" ");
            }
            if (caseQuery.getCreatedTo() != null) {
                sb.append(lang.to().toLowerCase()).append(" ").append(DateFormatter.formatDateTime(caseQuery.getCreatedTo())).append(" ");
            }
            managerElement.setInnerText(sb.toString());
            element.appendChild(managerElement);
        }
        // date ModifiedFrom ModifiedTo
        if (caseQuery.getModifiedFrom() != null || caseQuery.getModifiedTo() != null) {
            Element managerElement = DOM.createElement("p");
            StringBuilder sb = new StringBuilder();
            sb.append(lang.updated()).append(": ");
            if (caseQuery.getModifiedFrom() != null) {
                sb.append(lang.from().toLowerCase()).append(" ").append(DateFormatter.formatDateTime(caseQuery.getModifiedFrom())).append(" ");
            }
            if (caseQuery.getModifiedTo() != null) {
                sb.append(lang.to().toLowerCase()).append(" ").append(DateFormatter.formatDateTime(caseQuery.getModifiedTo())).append(" ");
            }
            managerElement.setInnerText(sb.toString());
            element.appendChild(managerElement);
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
            element.appendChild(managerElement);
        }

        // importance
        if (CollectionUtils.isNotEmpty(caseQuery.getImportanceIds())) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.issueImportance() + ": " +
                    caseQuery.getImportanceIds()
                            .stream()
                            .map(En_ImportanceLevel::getById)
                            .map(caseImportanceLang::getImportanceName)
                            .collect(Collectors.joining(", "))
            );
            element.appendChild(managerElement);
        }

        // states
        if (en_reportType == En_ReportType.PROJECT) {
            if (CollectionUtils.isNotEmpty(caseQuery.getStateIds())) {
                Element managerElement = DOM.createElement("p");
                managerElement.setInnerText(lang.issueState() + ": " +
                        caseQuery.getStateIds()
                                .stream()
                                .map(id -> En_RegionState.forId(id))
                                .map(regionStateLang::getStateName)
                                .collect(Collectors.joining(", "))
                );
                element.appendChild(managerElement);
            }
        } else if (CollectionUtils.isNotEmpty(caseQuery.getStateIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueState(), caseQuery.getStateIds()));
        }

        // companies
        if (CollectionUtils.isNotEmpty(caseQuery.getCompanyIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueCompany(), caseQuery.getCompanyIds()));
        }

        // products
        if (CollectionUtils.isNotEmpty(caseQuery.getProductIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueProduct(), caseQuery.getProductIds()));
        }

        // managers
        if (CollectionUtils.isNotEmpty(caseQuery.getManagerIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueManager(), caseQuery.getManagerIds()));
        }

        // tags
        if (CollectionUtils.isNotEmpty(caseQuery. getCaseTagsIds())) {
            element.appendChild(makeArraySelectedElement(lang.tags(), caseQuery.getCaseTagsIds()));
        }

        // authors
        if (CollectionUtils.isNotEmpty(caseQuery.getCommentAuthorIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueCommentAuthor(), caseQuery.getCommentAuthorIds()));
        }

        // project directions
        if (CollectionUtils.isNotEmpty(caseQuery.getProductDirectionIds())) {
            element.appendChild(makeArraySelectedElement(lang.productDirection(), caseQuery.getProductDirectionIds()));
        }
        // project region
        if (CollectionUtils.isNotEmpty(caseQuery.getRegionIds())) {
            element.appendChild(makeArraySelectedElement(lang.projectRegion(), caseQuery.getRegionIds()));
        }

        // project head manager
        if (CollectionUtils.isNotEmpty(caseQuery.getHeadManagerIds())) {
            element.appendChild(makeArraySelectedElement(lang.projectHeadManager(), caseQuery.getHeadManagerIds()));
        }

        // project team
        if (CollectionUtils.isNotEmpty(caseQuery.getCaseMemberIds())) {
            element.appendChild(makeArraySelectedElement(lang.projectTeam(), caseQuery.getCaseMemberIds()));
        }
    }

    private Element makeArraySelectedElement(String prefix, Collection<?> collection) {
        Element element = DOM.createElement("p");
        element.setInnerText(prefix + ": " + collection.size() + " " + lang.selected().toLowerCase());
        return element;
    }

    private Lang lang;
    private En_SortFieldLang sortFieldLang;
    private En_SortDirLang sortDirLang;
    private En_CaseImportanceLang caseImportanceLang;
    private En_RegionStateLang regionStateLang;
}
