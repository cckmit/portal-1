package ru.protei.portal.ui.documentation.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.query.DocumentationQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentationServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.documentation.client.activity.filter.AbstractDocumentationFilterActivity;
import ru.protei.portal.ui.documentation.client.activity.filter.AbstractDocumentationFilterView;

import java.util.List;


public abstract class DocumentationTableActivity
        implements Activity, AbstractDocumentationTableActivity, AbstractDocumentationFilterActivity,
        AbstractPagerActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setAnimation(animation);

        CREATE_ACTION = lang.buttonCreate();
        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());

        pagerView.setPageSize(view.getPageSize());
        pagerView.setActivity(this);

    }

    @Event
    public void onShow(DocumentationEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());
        init.parent.add(pagerView.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.DOCUMENTATION_CREATE) ?
                new ActionBarEvents.Add(CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.DOCUMENTATION) :
                new ActionBarEvents.Clear()
        );

        query = makeQuery();
        requestTotalCount();
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page + 1);
    }

    @Override
    public void onFirstClicked() {
        view.scrollTo(0);
    }

    @Override
    public void onLastClicked() {
        view.scrollTo(view.getPageCount() - 1);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.init = initDetails;
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DOCUMENTATION.equals(event.identity)) {
            return;
        }
        fireEvent(new DocumentationEvents.Edit());
    }

    @Override
    public void onEditClicked(Documentation value) {
        fireEvent(DocumentationEvents.Edit.byId(value.getId()));
    }

    @Override
    public void onFilterChanged() {
        query = makeQuery();
        requestTotalCount();
    }

    @Override
    public void onItemClicked(Documentation value) {
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Documentation>> callback) {
        query.setOffset(offset);
        query.setLimit(limit);

        documentationService.getDocumentations(query, new RequestCallback<List<Documentation>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess(List<Documentation> result) {
                callback.onSuccess(result);
            }
        });
    }

    private void requestTotalCount() {
        view.clearRecords();
        //animation.closeDetails();

        documentationService.getDocumentationCount(query, new RequestCallback<Long>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Long result) {
                view.setRecordCount(result);
                pagerView.setTotalPages(view.getPageCount());
            }
        });
    }

    private DocumentationQuery makeQuery() {
        return new DocumentationQuery();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDocumentationTableView view;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    AbstractDocumentationFilterView filterView;
    @Inject
    TableAnimation animation;
    @Inject
    DocumentationServiceAsync documentationService;
    @Inject
    PolicyService policyService;


    private static String CREATE_ACTION;
    private AppEvents.InitDetails init;
    private DocumentationQuery query;
}
