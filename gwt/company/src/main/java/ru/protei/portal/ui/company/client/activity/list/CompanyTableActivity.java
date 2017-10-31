package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.winter.web.common.client.events.SectionEvents;

import java.util.List;

/**
 * Активность таблицы компаний
 */
public abstract class CompanyTableActivity extends CompanyGridActivity implements
        AbstractPagerActivity, ClickColumn.Handler< Company >, EditClickColumn.EditHandler< Company >,
        InfiniteLoadHandler<Company>, InfiniteTableWidget.PagerListener {

    @PostConstruct
    public void init() {
        view.setActivity( this );
        view.setAnimation( animation );
        pagerView.setPageSize( view.getPageSize() );
        pagerView.setActivity( this );
    }

    @Event
    public void onShow( CompanyEvents.Show event ) {
        if(filterView.viewType().getValue() != ViewType.TABLE)
            return;

        init(this::requestCompaniesCount, view.asWidget(), pagerView.asWidget());
        view.getFilterContainer().add(filterView.asWidget());
        requestCompaniesCount();
    }

    @Event
    public void onCreateClicked( SectionEvents.Clicked event ) {
        if ( !(UiConstants.ActionBarIdentity.COMPANY.equals( event.identity ) && filterView.viewType().getValue() == ViewType.TABLE) ) {
            return;
        }

        fireEvent(new CompanyEvents.Edit(null));
    }

    @Event
    public void onChangeRow( CompanyEvents.ChangeCompany event ) {
        companyService.getCompany( event.companyId, new RequestCallback<Company>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Company company ) {
                view.updateRow(company);
            }
        } );
    }


    @Override
    public void onFirstClicked() {
        view.scrollTo( 0 );
    }

    @Override
    public void onLastClicked() {
        view.scrollTo( view.getPageCount()-1 );
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage( page + 1 );
    }

    @Override
    public void onItemClicked(Company value) {
        showPreview(value);
    }

    @Override
    public void onEditClicked(Company value) {
        fireEvent( new CompanyEvents.Edit ( value.getId() ));
    }

    private void showPreview (Company value ) {

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new CompanyEvents.ShowPreview( view.getPreviewContainer(), value, true ) );
        }
    }

    @Override
    public void loadData( int offset, int limit, AsyncCallback<List<Company>> asyncCallback ) {
        query.setOffset( offset );
        query.setLimit( limit );

        companyService.getCompanies(query, new RequestCallback< List <Company> >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( List< Company > companies ) {
                asyncCallback.onSuccess( companies );
            }
        });

    }

    private void requestCompaniesCount() {
        view.clearRecords();
        animation.closeDetails();

        companyService.getCompaniesCount(query, new RequestCallback< Long >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess( Long count ) {
                view.setIssuesCount( count );
                pagerView.setTotalPages( view.getPageCount() );
            }
        });
    }

    @Inject
    AbstractCompanyTableView view;

    @Inject
    TableAnimation animation;

    @Inject
    AbstractPagerView pagerView;

}
