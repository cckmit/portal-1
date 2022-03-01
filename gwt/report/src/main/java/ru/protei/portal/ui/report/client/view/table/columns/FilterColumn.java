package ru.protei.portal.ui.report.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.query.YoutrackWorkQuery;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.ui.common.client.columns.StaticColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.*;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isNotBlank;

public class FilterColumn extends StaticColumn<ReportDto> {

    @Inject
    public FilterColumn(Lang lang, En_SortFieldLang sortFieldLang, En_SortDirLang sortDirLang,
                        ProjectStateLang projectStateLang, En_DateIntervalLang intervalTypeLang,
                        En_ContractKindLang contractKindLang, En_ContractTypeLang contractTypeLang,
                        ContractStateLang contractStateLang) {
        this.lang = lang;
        this.sortFieldLang = sortFieldLang;
        this.sortDirLang = sortDirLang;
        this.projectStateLang = projectStateLang;
        this.intervalTypeLang = intervalTypeLang;
        this.contractKindLang = contractKindLang;
        this.contractTypeLang = contractTypeLang;
        this.contractStateLang = contractStateLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("filter");
        columnHeader.setInnerText(lang.issueReportsFilter());
    }

    @Override
    public void fillColumnValue(Element cell, ReportDto value) {
        cell.addClassName("filter");

        Element divElement = DOM.createDiv();

        if (value == null) {
            cell.appendChild(divElement);
            return;
        }

        if (value.getQuery() != null) {
            En_ReportType reportType = value.getReport().getReportType();
            switch (reportType) {
                case CASE_OBJECTS:
                case CASE_TIME_ELAPSED:
                case CASE_RESOLUTION_TIME:
                case NIGHT_WORK:
                    appendCaseQueryInfo(divElement, (CaseQuery) value.getQuery());
                    break;
                case CONTRACT:
                    appendContractQueryInfo(divElement, (ContractQuery) value.getQuery());
                    break;
                case PROJECT:
                    appendProjectQueryInfo(divElement, (ProjectQuery) value.getQuery());
                    break;
                case YT_WORK:
                    appendYoutrackWorkQueryInfo(divElement, (YoutrackWorkQuery) value.getQuery());
            }
        }

        cell.appendChild(divElement);
    }

    private void appendCaseQueryInfo(Element element, CaseQuery caseQuery) {

        // search string
        if (isNotBlank(caseQuery.getSearchString())) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.search() + ": " + caseQuery.getSearchString());
            element.appendChild(managerElement);
        }

        // createdRange
        if (caseQuery.getCreatedRange() == null || caseQuery.getCreatedRange().getIntervalType() == null) {
            //для совместимости с созданными ранее фильтрами
            //date CreatedFrom CreatedTo
            if (caseQuery.getCreatedFrom() != null || caseQuery.getCreatedTo() != null) {
                element.appendChild(makeDateRangeElement(
                        lang.created(),
                        caseQuery.getCreatedFrom(),
                        caseQuery.getCreatedTo()));
            }
        } else {
            element.appendChild(makeDateRangeElement(lang.created(), caseQuery.getCreatedRange()));
        }

        // modifiedRange
        if (caseQuery.getModifiedRange() == null || caseQuery.getModifiedRange().getIntervalType() == null) {
            //для совместимости с созданными ранее фильтрами
            //date ModifiedFrom ModifiedTo
            if (caseQuery.getModifiedFrom() != null || caseQuery.getModifiedTo() != null) {
                element.appendChild(makeDateRangeElement(
                        lang.updated(),
                        caseQuery.getModifiedFrom(),
                        caseQuery.getModifiedTo()));
            }
        } else {
            element.appendChild(makeDateRangeElement(lang.updated(), caseQuery.getModifiedRange()));
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
        if (isNotEmpty(caseQuery.getImportanceIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueImportance(), caseQuery.getImportanceIds()));
        }

        // states
        if (isNotEmpty(caseQuery.getStateIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueState(), caseQuery.getStateIds()));
        }

        // companies
        if (isNotEmpty(caseQuery.getCompanyIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueCompany(), caseQuery.getCompanyIds()));
        }

        // products
        if (isNotEmpty(caseQuery.getProductIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueProduct(), caseQuery.getProductIds()));
        }

        // managers
        if (isNotEmpty(caseQuery.getManagerIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueManager(), caseQuery.getManagerIds()));
        }

        // tags
        if (isNotEmpty(caseQuery. getCaseTagsIds())) {
            element.appendChild(makeArraySelectedElement(lang.tags(), caseQuery.getCaseTagsIds()));
        }

        // authors
        if (isNotEmpty(caseQuery.getCommentAuthorIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueCommentAuthor(), caseQuery.getCommentAuthorIds()));
        }

        // time elapsed types
        if (isNotEmpty(caseQuery.getTimeElapsedTypeIds())) {
            element.appendChild(makeArraySelectedElement(
                    lang.timeElapsedType(),
                    toSet(caseQuery.getTimeElapsedTypeIds(), En_TimeElapsedType::findById))
            );
        }
    }

    private void appendProjectQueryInfo(Element element, ProjectQuery projectQuery){

        // search string
        if (isNotBlank(projectQuery.getSearchString())) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.search() + ": " + projectQuery.getSearchString());
            element.appendChild(managerElement);
        }

        // sorting
        if (projectQuery.getSortField() != null) {
            Element managerElement = DOM.createElement("p");
            StringBuilder sb = new StringBuilder();
            sb.append(lang.sortBy()).append(": ").append(sortFieldLang.getName(projectQuery.getSortField()).toLowerCase());
            if (projectQuery.getSortDir() != null) {
                sb.append(" ").append(sortDirLang.getName(projectQuery.getSortDir()).toLowerCase());
            }
            managerElement.setInnerText(sb.toString());
            element.appendChild(managerElement);
        }

        // project directions
        if (isNotEmpty(projectQuery.getDirectionIds())) {
            element.appendChild(makeArraySelectedElement(lang.productDirection(), projectQuery.getDirectionIds()));
        }

        // states
        if (isNotEmpty(projectQuery.getStateIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueState(), projectQuery.getStateIds()));
        }

        // project region
        if (isNotEmpty(projectQuery.getRegionIds())) {
            element.appendChild(makeArraySelectedElement(lang.projectRegion(), projectQuery.getRegionIds()));
        }

        // project head manager
        if (isNotEmpty(projectQuery.getHeadManagerIds())) {
            element.appendChild(makeArraySelectedElement(lang.projectHeadManager(), projectQuery.getHeadManagerIds()));
        }

        // project team
        if (isNotEmpty(projectQuery.getCaseMemberIds())) {
            element.appendChild(makeArraySelectedElement(lang.projectTeam(), projectQuery.getCaseMemberIds()));
        }

        // companies
        if (isNotEmpty(projectQuery.getInitiatorCompanyIds())) {
            element.appendChild(makeArraySelectedElement(lang.issueCompany(), projectQuery.getInitiatorCompanyIds()));
        }

        // only mine
            Element onlyMineElement = DOM.createElement("p");
            onlyMineElement.setInnerText(lang.projectOnlyMine() + ": " +
                    (projectQuery.getMemberId() != null ? lang.yes() : lang.no()));
            element.appendChild(onlyMineElement);
    }

    private void appendContractQueryInfo(Element element, ContractQuery contractQuery) {

        // search string
        if (isNotBlank(contractQuery.getSearchString())) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.search() + ": " + contractQuery.getSearchString());
            element.appendChild(managerElement);
        }

        // sorting
        if (contractQuery.getSortField() != null) {
            Element managerElement = DOM.createElement("p");
            StringBuilder sb = new StringBuilder();
            sb.append(lang.sortBy()).append(": ").append(sortFieldLang.getName(contractQuery.getSortField()).toLowerCase());
            if (contractQuery.getSortDir() != null) {
                sb.append(" ").append(sortDirLang.getName(contractQuery.getSortDir()).toLowerCase());
            }
            managerElement.setInnerText(sb.toString());
            element.appendChild(managerElement);
        }

        // date signing range
        if (contractQuery.getDateSigningRange() != null) {
            element.appendChild(makeDateRangeElement(lang.contractDateSigning(), contractQuery.getDateSigningRange()));
        }

        // date valid range
        if (contractQuery.getDateValidRange() != null) {
            element.appendChild(makeDateRangeElement(lang.contractDateValid(), contractQuery.getDateValidRange()));
        }

        // kind
        if (contractQuery.getKind() != null) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.contractKind() + ": " + contractKindLang.getName(contractQuery.getKind()));
            element.appendChild(managerElement);
        }

        // types
        if (isNotEmpty(contractQuery.getTypes())) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.contractType() + ": " + stream(contractQuery.getTypes())
                    .map(type -> contractTypeLang.getName(type))
                    .collect(Collectors.joining(", ")));
            element.appendChild(managerElement);
        }

        // states
        if (isNotEmpty(contractQuery.getStates())) {
            Element managerElement = DOM.createElement("p");
            managerElement.setInnerText(lang.contractState() + ": " + stream(contractQuery.getStates())
                    .map(state -> contractStateLang.getName(state.getState()))
                    .collect(Collectors.joining(", ")));
            element.appendChild(managerElement);
        }

        // organization
        if (isNotEmpty(contractQuery.getOrganizationIds())) {
            element.appendChild(makeArraySelectedElement(lang.contractOrganization(), contractQuery.getOrganizationIds()));
        }

        // contractors
        if (isNotEmpty(contractQuery.getContractorIds())) {
            element.appendChild(makeArraySelectedElement(lang.contractContractor(), contractQuery.getContractorIds()));
        }

        // managers
        if (isNotEmpty(contractQuery.getManagerIds())) {
            element.appendChild(makeArraySelectedElement(lang.contractProjectManager(), contractQuery.getManagerIds()));
        }
    }

    private void appendYoutrackWorkQueryInfo(Element element, YoutrackWorkQuery query){
        element.appendChild(makeDateRangeElement(lang.period(), query.getDateRange()));
    }

    private Element makeDateRangeElement(String name, Date from, Date to) {
        Element dateRangeElement = DOM.createElement("p");
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ");
        if (from != null) {
            sb.append(lang.from().toLowerCase()).append(" ")
              .append(DateFormatter.formatDateTime(from)).append(" ");
        }
        if (to != null) {
            sb.append(lang.to().toLowerCase()).append(" ")
              .append(DateFormatter.formatDateTime(to)).append(" ");
        }
        dateRangeElement.setInnerText(sb.toString());
        return dateRangeElement;
    }

    private Element makeDateRangeElement(String name, DateRange range) {
        Element dateRangeElement = DOM.createElement("p");
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ");

        if (Objects.equals(En_DateIntervalType.FIXED, range.getIntervalType())) {
            if (range.getFrom() != null) {
                sb.append(lang.from().toLowerCase()).append(" ")
                  .append(DateFormatter.formatDateTime(range.getFrom())).append(" ");
            }
            if (range.getTo() != null) {
                sb.append(lang.to().toLowerCase()).append(" ")
                  .append(DateFormatter.formatDateTime(range.getTo())).append(" ");
            }
        } else {
            sb.append(intervalTypeLang.getName(range.getIntervalType())).append(" ");
        }

        dateRangeElement.setInnerText(sb.toString());
        return dateRangeElement;
    }

    private Element makeArraySelectedElement(String prefix, Collection<?> collection) {
        Element element = DOM.createElement("p");
        element.setInnerText(prefix + ": " + collection.size() + " " + lang.selected().toLowerCase());
        return element;
    }

    private Lang lang;
    private En_SortFieldLang sortFieldLang;
    private En_SortDirLang sortDirLang;
    private ProjectStateLang projectStateLang;
    private En_DateIntervalLang intervalTypeLang;
    private En_ContractKindLang contractKindLang;
    private En_ContractTypeLang contractTypeLang;
    private ContractStateLang contractStateLang;
}
