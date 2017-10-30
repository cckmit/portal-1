package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterActivity;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterView;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemActivity;
import ru.protei.portal.ui.company.client.activity.item.AbstractCompanyItemView;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Активность списка компаний
 */
public abstract class CompanyGridActivity implements AbstractCompanyGridActivity, AbstractCompanyFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();
        filterView.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void init(Widget... widgets) {
        fireEvent(new AppEvents.InitPanelName(lang.companies()));
        init.parent.clear();
        for(Widget widget: widgets){
            init.parent.add(widget);
        }

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.COMPANY_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.COMPANY ) :
                new ActionBarEvents.Clear()
        );

        query = makeQuery();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !UiConstants.ActionBarIdentity.COMPANY.equals( event.identity ) ) {
            return;
        }

        fireEvent(new CompanyEvents.Edit(null));
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails event ) {
        this.init = event;
    }

    @Override
    public void onCreateClicked( ) { fireEvent(new CompanyEvents.Edit()); }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
//        requestCompanies();
    }

    protected CompanyQuery makeQuery() {
        query = new CompanyQuery(filterView.searchPattern().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC);

        if(filterView.categories().getValue() != null)
            query.setCategoryIds(
                    filterView.categories().getValue()
                            .stream()
                            .map( EntityOption::getId )
                            .collect( Collectors.toList() ));

        if(filterView.group().getValue() != null)
            query.setGroupId(filterView.group().getValue().getId());

        return query;
    }


    @Inject
    AbstractCompanyFilterView filterView;
    @Inject
    Lang lang;

    @Inject
    CompanyServiceAsync companyService;
    @Inject
    PolicyService policyService;


    private AppEvents.InitDetails init;
    private static String CREATE_ACTION;

    protected CompanyQuery query;
}