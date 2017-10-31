package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyServiceAsync;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterActivity;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterView;

import java.util.stream.Collectors;

/**
 * Активность множества компаний
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
    public void init(Runnable onFilterChangedAction, Widget... widgets) {
        fireEvent(new AppEvents.InitPanelName(lang.companies()));
        init.parent.clear();

        this.onFilterChangedAction = onFilterChangedAction;
        for(Widget widget: widgets){
            init.parent.add(widget);
        }

        fireEvent( policyService.hasPrivilegeFor( En_Privilege.COMPANY_CREATE ) ?
                new ActionBarEvents.Add( CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.COMPANY ) :
                new ActionBarEvents.Clear()
        );

        query = makeQuery();
        currentViewType = filterView.viewType().getValue();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails event ) {
        this.init = event;
    }

    @Override
    public void onFilterChanged() {
        if(filterView.viewType().getValue() != currentViewType){
            fireEvent(new CompanyEvents.Show());
            return;
        }

        query = makeQuery();
        if(onFilterChangedAction != null)
            onFilterChangedAction.run();
    }

    private CompanyQuery makeQuery() {
        CompanyQuery cq = new CompanyQuery(filterView.searchPattern().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC);

        if(filterView.categories().getValue() != null)
            cq.setCategoryIds(
                    filterView.categories().getValue()
                            .stream()
                            .map( EntityOption::getId )
                            .collect( Collectors.toList() ));

        if(filterView.group().getValue() != null)
            cq.setGroupId(filterView.group().getValue().getId());

        return cq;
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
    private static Runnable onFilterChangedAction;
    private static String CREATE_ACTION;
    private static ViewType currentViewType;

    protected static CompanyQuery query;
}