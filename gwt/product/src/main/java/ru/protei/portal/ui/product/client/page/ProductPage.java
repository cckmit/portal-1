package ru.protei.portal.ui.product.client.page;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.winter.web.common.client.events.MenuEvents;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * Активность по работе с вкладкой "Продукты"
 */
public abstract class ProductPage
        implements Activity {

    @PostConstruct
    public void onInit() {
        ТAB = lang.complexesAndProducts();
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        if ( event.profile.hasPrivilegeFor( En_Privilege.PRODUCT_VIEW ) ) {
            fireEvent( new MenuEvents.Add( ТAB, UiConstants.TabIcons.PRODUCT, ТAB,
                                           CrmConstants.PAGE_LINK.PRODUCT,
                                           DebugIds.SIDEBAR_MENU.PRODUCT ) );
            fireEvent( new AppEvents.InitPage( new ProductEvents.Show( false ) ) );
        }
    }

    @Event
    public void onShowTable( ProductEvents.Show event ) {
        fireSelectTab();
    }

    @Event
    public void onShowDetail( ProductEvents.Edit event ) {
        fireSelectTab();
    }

    @Event
    public void onShowPreview(ProductEvents.ShowFullScreen event) {
        fireSelectTab();
    }

    @Event
    public void onClickSection( SectionEvents.Clicked event ) {
        if ( !ТAB.equals( event.identity ) ) {
            return;
        }

        fireSelectTab();
        fireEvent( new ProductEvents.Show( false ) );
    }

    private void fireSelectTab() {
        fireEvent( new ActionBarEvents.Clear() );
        if ( policyService.hasPrivilegeFor( En_Privilege.PRODUCT_VIEW ) ) {
            fireEvent(new MenuEvents.Select(ТAB));
        }
    }


    @Inject
    Lang lang;
    @Inject
    private PolicyService policyService;

    private String ТAB;
}

