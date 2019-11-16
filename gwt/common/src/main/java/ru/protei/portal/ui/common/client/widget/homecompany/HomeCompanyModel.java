package ru.protei.portal.ui.common.client.widget.homecompany;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.widget.components.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

/**
 * Модель домашних компаний
 */
public abstract class HomeCompanyModel implements Activity,
        ru.protei.portal.ui.common.client.widget.components.client.selector.AsyncSelectorModel<EntityOption> {

    @Event
    public void onInit( AuthEvents.Success event ) {
        clear();
    }

    @Override
    public EntityOption get( int elementIndex, LoadingHandler handler ) {
        if(list==null)refreshOptions(reverseOrder, handler);
        if (size( list ) <= elementIndex) return null;
        return list.get( elementIndex );
    }

    public void setReverseOrder( boolean reverseOrder ) {//TODO
        this.reverseOrder = reverseOrder;
    }

    public void clear(  ) {
        list = null;
    }

    private void refreshOptions( boolean reverseOrder, LoadingHandler handler) {
        handler.onLoadingStart();
        list = new ArrayList<>(  );
        companyService.getCompanyOptionList(new CompanyQuery(true).onlyVisibleFields().reverseOrder(reverseOrder),
                new FluentCallback<List<EntityOption>>()
                        .withSuccess(companies -> {
                            list.clear();
                            list.addAll(companies);
                            handler.onLoadingComplete();
                        }));
    }

    @Inject
    private CompanyControllerAsync companyService;

    private List< EntityOption > list;
    private boolean reverseOrder;
}
