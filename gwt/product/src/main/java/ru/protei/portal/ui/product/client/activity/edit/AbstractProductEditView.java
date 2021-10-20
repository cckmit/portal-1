package ru.protei.portal.ui.product.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.gwt.crypto.util.Str;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.view.PersonShortView;
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
    void setActivity(AbstractProductEditActivity activity);

    void setCurrentProduct(ProductShortView product);

    HasValue<String> name();

    void setTypeAndDirections(En_DevUnitType type, Set<ProductDirectionInfo> directions);

    Pair<En_DevUnitType, Set<ProductDirectionInfo>> getTypeAndDirections();

    HasVisibility typeVisibility();

    HasValidable nameValidator();

    HasValue<String> info();

    HasValue<Set<ProductShortView>> parents();

    HasValue<Set<ProductShortView>> children();

    void setNameStatus(NameStatus status);

    void setInfoPreviewAllowed(boolean isPreviewAllowed);

    void setTypeImage(String src, String title);

    void setTypeImageVisibility(boolean isVisible);

    HasValue<String> internalDocLink();

    HasValue<String> externalDocLink();

    HasValue<String> historyVersion();

    HasValue<String> configuration();

    HasValue<String> cdrDescription();

    HasValue<List<Subscription>> productSubscriptions();

    HasVisibility directionContainerVisibility();

    void directionSelectorVisibility(boolean isMulti);

    void setHistoryVersionPreviewAllowing(boolean isPreviewAllowed);

    void setConfigurationPreviewAllowing(boolean isPreviewAllowed);

    void setCdrDescriptionPreviewAllowed(boolean isPreviewAllowed);

    HasValue<List<String>> aliases();

    HasVisibility aliasesVisibility();

    void setCommonManagerCompanyId(Long id);

    HasValue<PersonShortView> commonManager();

    HasVisibility commonManagerContainerVisibility();

    HasVisibility parentsContainerVisibility();

    void makeChildrenContainerShortView();

    void makeChildrenContainerFullView();

    void makeDirectionContainerShortView();

    void makeDirectionContainerFullView();

    void setParentTypes(En_DevUnitType... types);

    void setChildrenTypes(En_DevUnitType... types);

    void setNameLabel(String label);

    void setDescriptionLabel(String label);

    void setChildrenContainerLabel(String label);

    String HISTORY_VERSION = "historyVersion";
    String CONFIGURATION = "configuration";
    String CDR_DESCRIPTION = "cdr_description";
    String INFO = "info";
}
