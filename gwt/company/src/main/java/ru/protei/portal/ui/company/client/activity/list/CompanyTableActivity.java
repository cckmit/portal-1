package ru.protei.portal.ui.company.client.activity.list;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CompanyEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Активность таблицы компаний
 */
public abstract class CompanyTableActivity implements
        Activity, AbstractCompanyTableActivity {

    @PostConstruct
    public void init() {
        view.setActivity( this );
        view.setAnimation( animation );
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event
    public void onShow( CompanyEvents.ShowDefinite event ) {
        if(event.viewType != ViewType.TABLE)
            return;

        this.query = event.query;
        init.parent.clear();
        init.parent.add( view.asWidget() );

        view.getFilterContainer().add(event.filter);
        requestCompanies();
    }

    @Event
    public void onFilterChange( CompanyEvents.UpdateData event ) {
        if(event.viewType != ViewType.TABLE)
            return;

        this.query = event.query;
        requestCompanies();
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
            fireEvent( new CompanyEvents.ShowPreview( view.getPreviewContainer(), value, true, true ) );
        }
    }

    private void requestCompanies() {
        view.clearRecords();
        animation.closeDetails();

        companyService.getCompanies(query, new RequestCallback<List<Company>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Company> result) {
                view.setData(result);
            }
        });
    }

    @Inject
    AbstractCompanyTableView view;
    @Inject
    TableAnimation animation;
    @Inject
    Lang lang;
    @Inject
    CompanyControllerAsync companyService;

    private AppEvents.InitDetails init;
    private CompanyQuery query;

}
