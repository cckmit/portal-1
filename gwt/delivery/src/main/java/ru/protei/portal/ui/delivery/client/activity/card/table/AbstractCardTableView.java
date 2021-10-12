package ru.protei.portal.ui.delivery.client.activity.card.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.widget.stringselectpopup.StringSelectPopup;

import java.util.Set;

public interface AbstractCardTableView extends IsWidget {
    void setActivity(AbstractCardTableActivity cardTableActivity);
    void setAnimation(TableAnimation animation);

    void clearRecords();
    void triggerTableLoad();
    void setTotalRecords(int totalRecords);
    int getPageCount();
    void scrollTo(int page);

    HasWidgets getPreviewContainer();
    HasWidgets getFilterContainer();
    HasWidgets getPagerContainer();

    StringSelectPopup getPopup();

    void clearSelection();

    void updateRow(Card item);

    Set<Card> getSelectedCards();

    void clearSelectedRows();

    void setGroupButtonEnabled(boolean isEnabled);
}
