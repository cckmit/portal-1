package ru.protei.portal.ui.issue.client.util;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Утилита по работе с пользовательскими фильтрами
 */
public class IssueFilterUtils {

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

    public static Set< PersonShortView > getManagers( List< Long > managerIds ) {

        if ( managerIds == null || managerIds.isEmpty() ) {
            return null;
        }
        Set< PersonShortView > managers = new HashSet<>();
        for ( Long id : managerIds ) {
            PersonShortView person = new PersonShortView();
            person.setId( id );
            managers.add( person );
        }
        return managers;
    }

    public static Set< PersonShortView > getInitiators( List< Long > initiatorsIds ) {

        if ( initiatorsIds == null || initiatorsIds.isEmpty() ) {
            return null;
        }
        Set< PersonShortView > initiators = new HashSet<>();
        for ( Long id : initiatorsIds ) {
            PersonShortView person = new PersonShortView();
            person.setId( id );
            initiators.add( person );
        }
        return initiators;
    }
}
