package ru.protei.portal.ui.common.client.util;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;

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


    public static Set< EntityOption > getOptions( Collection<Long> ids ) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return ids.stream().map(id -> {
            EntityOption option = new EntityOption();
            option.setId(id);
            return option;
        }).collect(Collectors.toSet());
    }


    public static List< Long > getIds( Collection<EntityOption> options ) {
        if (CollectionUtils.isEmpty(options)) {
            return null;
        }
        return options.stream().map(EntityOption::getId).collect(Collectors.toList());
    }


    public static Set< CaseState > getStates( List< Long > statesIdList ) {
        if ( statesIdList == null || statesIdList.isEmpty() ) {
            return null;
        }
        Set< CaseState > states = new HashSet<>();
        for ( Long id : statesIdList ) {
            states.add( new CaseState( id ) );
        }
        return states;
    }

    public static List< CaseState > getStateList( Set< CaseState > stateSet ) {

        if ( stateSet == null || stateSet.isEmpty() ) {
            return null;
        }
        List< CaseState > list = new ArrayList<>(  );
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
            option.setDisplayText( company.getCname() );
        return option;
    }

    public static EntityOption toEntityOption( CaseTag tag, boolean isProtei ) {
        if ( tag == null  ) {
            return null;
        }
        EntityOption option = new EntityOption();
        option.setId( tag.getId() );
        option.setDisplayText(tag.getName());
        option.setInfo(isProtei? tag.getCompanyName() : null);
        return option;
    }

    public static Set< Long > getProductsIdList( Set< ProductShortView > productSet ) {

        if ( productSet == null || productSet.isEmpty() ) {
            return null;
        }
        return productSet
                .stream()
                .map( ProductShortView::getId )
                .collect( Collectors.toSet() );
    }

    public static Set< ProductShortView > getProducts( Set< Long > productIds ) {

        if ( productIds == null || productIds.isEmpty() ) {
            return null;
        }
        Set< ProductShortView > products = new HashSet<>();
        for ( Long id : productIds ) {
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
        personsIds.forEach(id -> persons.add(new PersonShortView(null, id)));

        return persons;
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
