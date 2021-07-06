package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.tab.multi.MultiTabWidget;

import java.util.List;
import java.util.Set;

/**
 * Абстракция вида карточки редактирования Поставки
 */
public interface AbstractDeliveryEditView extends IsWidget {

    void setActivity(AbstractDeliveryEditActivity activity);

    HasWidgets getNameContainer();

    HasWidgets getMetaContainer();

    HasWidgets getItemsContainer();

    MultiTabWidget<En_CommentOrHistoryType> getMultiTabWidget();

    HasVisibility backButtonVisibility();

    HasVisibility showEditViewButtonVisibility();

    void setPreviewStyles(boolean isPreview);

    void fillKits(List<Kit> kitSet);

    HasValue<String> searchKitPattern();

    void setKitFilter(Selector.SelectorFilter<Kit> filter);

    Set<Kit> getSelectedKits();

    HasVisibility nameAndDescriptionEditButtonVisibility();

    void setCreatedBy(String value);

    HasVisibility addKitsButtonVisibility();
}
