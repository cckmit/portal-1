package ru.protei.portal.ui.delivery.client.activity.pcborder.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public interface AbstractPcbOrderTableView extends IsWidget {
    void setActivity(AbstractPcbOrderTableActivity activity);

    void addRecords(List<PcbOrder> pcbOrders, Comparator<Map.Entry<PcbOrderGroupType, List<PcbOrder>>> comparator);

    void clearRecords();
    HasWidgets getPagerContainer();

    HasWidgets getPreviewContainer();

    HasWidgets getFilterContainer();

    void setAnimation(TableAnimation animation);
}
