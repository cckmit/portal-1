package ru.protei.portal.ui.product.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.List;
import java.util.Set;

/**
 * Абстракция вида карточки создания/редактирования продукта
 */
public interface AbstractProductEditView extends IsWidget {

    void setActivity( AbstractProductEditActivity activity );

    void setCurrentProduct(ProductShortView product);

    HasValue<String> name();

    HasValue<En_DevUnitType> type();

    HasValidable nameValidator();

    HasValue<String> info();

    HasValue<Set<ProductShortView>> parents();

    HasValue<Set<ProductShortView>> components();

    void setNameStatus ( NameStatus status );

    void setIsProduct(boolean isProduct);

    HasValue<String> wikiLink();

    HasValue<String> historyVersion();

    HasValue<String> configuration();

    HasValue<String> cdrDescription();

    HasEnabled saveEnabled();

    HasValue<List<Subscription>> productSubscriptions();
    HasValidable productSubscriptionsValidator();

    void setHistoryVersionPreviewAllowing( boolean isPreviewAllowed );

    void setConfigurationPreviewAllowing( boolean isPreviewAllowed );

    void setCdrDescriptionPreviewAllowed( boolean isPreviewAllowed );

    String HISTORY_VERSION = "historyVersion";
    String CONFIGURATION = "configuration";
    String CDR_DESCRIPTION = "cdr_description";
}
