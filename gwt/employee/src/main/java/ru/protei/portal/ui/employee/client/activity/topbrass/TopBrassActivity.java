package ru.protei.portal.ui.employee.client.activity.topbrass;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.service.AvatarUtils;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.client.util.TopBrassPersonIdsUtil;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.employee.client.activity.item.AbstractTopBrassItemActivity;
import ru.protei.portal.ui.employee.client.activity.item.AbstractTopBrassItemView;
import ru.protei.winter.core.utils.beans.SearchResult;

public abstract class TopBrassActivity implements Activity, AbstractTopBrassActivity, AbstractTopBrassItemActivity {

    @Inject
    public void init() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails event ) {
        this.init = event;
    }

    @Event
    public void onTopBrassShow(EmployeeEvents.ShowTopBrass event) {
        init.parent.clear();
        init.parent.add(view.asWidget());

        view.topContainer().clear();
        view.bottomContainer().clear();

        employeeService.getEmployees(employeeQuery, new FluentCallback<SearchResult<EmployeeShortView>>()
                .withSuccess(this::fillView)
        );
    }

    @Override
    public void onBackButtonClicked() {
        fireEvent(new EmployeeEvents.Show());
    }

    private void fillView(SearchResult<EmployeeShortView> heads) {
        heads.getResults().forEach(this::fillView);
    }

    private void fillView(EmployeeShortView head) {
        AbstractTopBrassItemView itemView = makeItem(head);

        if (TopBrassPersonIdsUtil.getTopIds().contains(head.getId())) {
            view.topContainer().add(itemView.asWidget());
            itemView.addRootStyle("col-md-6");
        } else {
            view.bottomContainer().add(itemView.asWidget());
            itemView.addRootStyle("col-md-4");
        }
    }

    private AbstractTopBrassItemView makeItem(EmployeeShortView head) {
        AbstractTopBrassItemView itemView = provider.get();
        itemView.setActivity(this);
        itemView.setImage(AvatarUtils.getPhotoUrl(head.getId()));
        itemView.setName(head.getDisplayName());
        itemView.setPosition(head.getWorkerEntries().iterator().next().getPositionName());

        return itemView;
    }

    @Inject
    AbstractTopBrassView view;

    @Inject
    EmployeeControllerAsync employeeService;

    @Inject
    Provider<AbstractTopBrassItemView> provider;

    private final EmployeeQuery employeeQuery = new EmployeeQuery(TopBrassPersonIdsUtil.getPersonIds());

    private AppEvents.InitDetails init;
}
