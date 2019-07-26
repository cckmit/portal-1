package ru.protei.portal.ui.company.client.activity.list;

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
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterActivity;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterView;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.stream.Collectors;

/**
 * Активность множества компаний
 */
public abstract class CompanyGridActivity implements AbstractCompanyGridActivity, AbstractCompanyFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        filterView.setActivity( this );
        query = makeQuery();
        currentViewType = ViewType.TABLE;
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( CompanyEvents.Show event ) {
        fireEvent(new AppEvents.InitPanelName(lang.companies()));

        fireEvent(new ActionBarEvents.Clear());
        if(policyService.hasPrivilegeFor( En_Privilege.COMPANY_CREATE )){
            fireEvent(new ActionBarEvents.Add( lang.buttonCreate(), UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.COMPANY ));
        }

        boolean isListCurrent = currentViewType == ViewType.LIST;
        fireEvent(new ActionBarEvents.Add(
                isListCurrent? lang.table(): lang.list(),
                isListCurrent? UiConstants.ActionBarIcons.TABLE: UiConstants.ActionBarIcons.LIST,
                UiConstants.ActionBarIdentity.COMPANY_TYPE_VIEW
        ));

        fireEvent(new CompanyEvents.ShowDefinite(currentViewType, filterView.asWidget(), query));
    }

    @Event
    public void onChangeViewClicked( ActionBarEvents.Clicked event ) {
        if ( !(UiConstants.ActionBarIdentity.COMPANY_TYPE_VIEW.equals( event.identity )))
            return;

        currentViewType = currentViewType == ViewType.TABLE? ViewType.LIST: ViewType.TABLE;
        onShow(new CompanyEvents.Show());
    }

    @Event
    public void onCreateClicked( ActionBarEvents.Clicked event ) {
        if ( !(UiConstants.ActionBarIdentity.COMPANY.equals( event.identity )) ) {
            return;
        }

        fireEvent(new CompanyEvents.Edit(null));
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        fireEvent(new CompanyEvents.UpdateData(currentViewType, query));
    }

    private CompanyQuery makeQuery() {
        CompanyQuery cq = new CompanyQuery(filterView.searchPattern().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC, filterView.showDeprecated().getValue());

        if(filterView.categories().getValue() != null)
            cq.setCategoryIds(
                    filterView.categories().getValue()
                            .stream()
                            .map( EntityOption::getId )
                            .collect( Collectors.toList() ));

        return cq;
    }


    @Inject
    AbstractCompanyFilterView filterView;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    private ViewType currentViewType;
    private CompanyQuery query;
}