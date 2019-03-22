package ru.protei.portal.ui.common.client.util;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

/**
 * Утилита по работе с пользовательскими фильтрами
 */
public class IssueFilterUtils {

    public static final RegExp caseNumbersPattern = RegExp.compile("(\\d+,?\\s?)+");

    public static Set< En_ImportanceLevel > getImportances( List< Integer > importancesIdList ) {
        if ( importancesIdList == null || importancesIdList.isEmpty() ) {
            return null;
        }
        return importancesIdList
                .stream()
                .map( En_ImportanceLevel::getById )
                .collect( Collectors.toSet() );
    }

    public static List< Integer > getImportancesIdList( Set< En_ImportanceLevel > importanceSet ) {

        if ( importanceSet == null || importanceSet.isEmpty() ) {
            return null;
        }
        return importanceSet
                .stream()
                .map( En_ImportanceLevel::getId )
                .collect( Collectors.toList() );
    }


    public static Set< En_CaseState > getStates( List< Integer > statesIdList ) {
        if ( statesIdList == null || statesIdList.isEmpty() ) {
            return null;
        }
        Set< En_CaseState > states = new HashSet<>();
        for ( Integer id : statesIdList ) {
            states.add( En_CaseState.getById( Long.valueOf( id ) ) );
        }
        return states;
    }

    public static List< En_CaseState > getStateList( Set< En_CaseState > stateSet ) {

        if ( stateSet == null || stateSet.isEmpty() ) {
            return null;
        }
        List< En_CaseState > list = new ArrayList<>(  );
        list.addAll( stateSet );
        return list;
    }

    public static List< Long > getCompaniesIdList( Set< EntityOption > companySet ) {

        if ( companySet == null || companySet.isEmpty() ) {
            return null;
        }
        return companySet
                .stream()
                .map( EntityOption::getId )
                .collect( Collectors.toList() );
    }

    public static Set< EntityOption > getCompanies( List< Long > companyIds ) {

        if ( companyIds == null || companyIds.isEmpty() ) {
            return null;
        }
        Set< EntityOption > companies = new HashSet<>();
        for ( Long id : companyIds ) {
            EntityOption company = new EntityOption();
            company.setId( id );
            companies.add( company );
        }
        return companies;
    }

    public static EntityOption toEntityOption( Company company ) {
        if ( company == null  ) {
            return null;
        }
            EntityOption option = new EntityOption();
            option.setId( company.getId() );
        return option;
    }

    public static List< Long > getProductsIdList( Set< ProductShortView > productSet ) {

        if ( productSet == null || productSet.isEmpty() ) {
            return null;
        }
        return productSet
                .stream()
                .map( ProductShortView::getId )
                .collect( Collectors.toList() );
    }

    public static Set< ProductShortView > getProducts( List< Long > managerIds ) {

        if ( managerIds == null || managerIds.isEmpty() ) {
            return null;
        }
        Set< ProductShortView > products = new HashSet<>();
        for ( Long id : managerIds ) {
            ProductShortView prd = new ProductShortView();
            prd.setId( id );
            products.add( prd );
        }
        return products;
    }

    public static List< Long > getManagersIdList( Set< PersonShortView > personSet ) {

        if ( personSet == null || personSet.isEmpty() ) {
            return null;
        }
        return personSet
                .stream()
                .map( PersonShortView::getId )
                .collect( Collectors.toList() );
    }

    public static Set<PersonShortView> getPersons(List<Long> personsIds) {
        if (CollectionUtils.isEmpty(personsIds)) {
            return null;
        }
        Set<PersonShortView> persons = new HashSet<>();
        for (Long id : personsIds) {
            PersonShortView person = new PersonShortView();
            person.setId(id);
            persons.add(person);
        }
        return persons;
    }

    public static CaseQuery makeCaseQuery(AbstractIssueFilterWidgetView filterWidgetView, boolean isFillSearchString) {
        CaseQuery query = new CaseQuery();
        query.setType(En_CaseType.CRM_SUPPORT);
        if (isFillSearchString) {
            String searchString = filterWidgetView.searchPattern().getValue();
            query.setCaseNumbers( searchCaseNumber( searchString, filterWidgetView.searchByComments().getValue() ) );
            if (query.getCaseNumbers() == null) {
                query.setSearchStringAtComments(filterWidgetView.searchByComments().getValue());
                query.setSearchString( isBlank( searchString ) ? null : searchString );
            }
        }
        query.setViewPrivate(filterWidgetView.searchPrivate().getValue());
        query.setSortField(filterWidgetView.sortField().getValue());
        query.setSortDir(filterWidgetView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setCompanyIds(getCompaniesIdList(filterWidgetView.companies().getValue()));
        query.setProductIds(getProductsIdList(filterWidgetView.products().getValue()));
        query.setManagerIds(getManagersIdList(filterWidgetView.managers().getValue()));
        query.setInitiatorIds(getManagersIdList(filterWidgetView.initiators().getValue()));
        query.setImportanceIds(getImportancesIdList(filterWidgetView.importances().getValue()));
        query.setStates(getStateList(filterWidgetView.states().getValue()));
        query.setCommentAuthorIds(getManagersIdList(filterWidgetView.commentAuthors().getValue()));
        DateInterval createdInterval = filterWidgetView.dateCreatedRange().getValue();
        if (createdInterval != null) {
            query.setCreatedFrom(createdInterval.from);
            query.setCreatedTo(createdInterval.to);
        }
        DateInterval modifiedInterval = filterWidgetView.dateModifiedRange().getValue();
        if (modifiedInterval != null) {
            query.setModifiedFrom(modifiedInterval.from);
            query.setModifiedTo(modifiedInterval.to);
        }
        return query;
    }

    public static CaseQuery fillCreatedInterval( CaseQuery query, DateInterval interval ) {
        if (interval != null) {
            query.setCreatedFrom(interval.from);
            query.setCreatedTo(interval.to);
        }
        return query;
    }

    public static CaseQuery fillModifiedInterval( CaseQuery query, DateInterval interval ) {
        if (interval != null) {
            query.setModifiedFrom(interval.from);
            query.setModifiedTo(interval.to);
        }
        return query;
    }


    public static List<Long> searchCaseNumber( String searchString, boolean searchByComments ) {
        if (isBlank( searchString ) || searchByComments) {
            return null;
        }

        MatchResult result = caseNumbersPattern.exec( searchString );
        if (result != null && result.getGroup( 0 ).equals( searchString )) {
            return Arrays.stream( searchString.split( "," ) )
                    .map( cn -> Long.parseLong( cn.trim() ) )
                    .collect( Collectors.toList() );
        }

        return null;

    }
}
