package ru.protei.portal.ui.company.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterActivity;
import ru.protei.portal.ui.company.client.activity.filter.AbstractCompanyFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Активность таблицы компаний
 */
public abstract class CompanyTableActivity implements
        Activity, AbstractCompanyTableActivity, AbstractCompanyFilterActivity, AbstractPagerActivity {

    @PostConstruct
    public void init() {
        view.setActivity( this );
        view.setAnimation( animation );
        filterView.setActivity( this );
        pagerView.setActivity( this );

        view.getFilterContainer().add(filterView.asWidget());

        query = makeQuery();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onShow( CompanyEvents.Show event ) {
        fireEvent(new ActionBarEvents.Clear());
        if(policyService.hasPrivilegeFor( En_Privilege.COMPANY_CREATE )){
            fireEvent(new ActionBarEvents.Add( lang.buttonCreate(), null, UiConstants.ActionBarIdentity.COMPANY ));
        }

        init.parent.clear();
        init.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );

        loadTable();
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
        loadTable();
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onItemClicked(Company value) {
        showPreview(value);
    }

    @Override
    public void onEditClicked(Company value) {
        if (!value.isArchived()) {
            fireEvent(new CompanyEvents.Edit(value.getId()));
        }
    }

    @Override
    public void onArchiveClicked(Company value) {
        if (value == null) {
            return;
        }

        companyService.updateState(value.getId(), !value.isArchived(), new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    loadTable();
                    fireEvent(new NotifyEvents.Show(lang.msgStatusChanged(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new CompanyEvents.ChangeModel());
                }));
    }

    private void showPreview(Company value) {

        if (value == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new CompanyEvents.ShowPreview(view.getPreviewContainer(), value, true));
        }
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Company>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        query.setOffset(offset);
        query.setLimit(limit);
        companyService.getCompanies(query, new FluentCallback<SearchResult<Company>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    asyncCallback.onSuccess(sr.getResults());
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                    }
                }));
    }

    private CompanyQuery makeQuery() {
        CompanyQuery cq = new CompanyQuery(filterView.searchPattern().getValue(),
                filterView.sortField().getValue(),
                filterView.sortDir().getValue()? En_SortDir.ASC: En_SortDir.DESC,
                true // !!TODO dont known if its needed here
                );

        if(filterView.categories().getValue() != null)
            cq.setCategoryIds(
                    filterView.categories().getValue()
                            .stream()
                            .map( EntityOption::getId )
                            .collect( Collectors.toList() ));

        return cq;
    }


    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    @Inject
    AbstractCompanyTableView view;
    @Inject
    AbstractCompanyFilterView filterView;
    @Inject
    TableAnimation animation;
    @Inject
    Lang lang;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    CompanyControllerAsync companyService;
    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails init;
    private CompanyQuery query;
}
