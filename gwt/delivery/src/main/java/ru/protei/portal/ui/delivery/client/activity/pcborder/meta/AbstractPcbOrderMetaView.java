package ru.protei.portal.ui.delivery.client.activity.pcborder.meta;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.ImportanceLevel;

import java.util.Date;

public interface AbstractPcbOrderMetaView extends IsWidget {

    void setActivity(AbstractPcbOrderMetaActivity activity);

//    HasValue<CaseState> state();
//
//    HasEnabled stateEnable();
//
//    HasValue<ImportanceLevel> priority();
//
//    HasValue<Date> deadline();
//
//    HasEnabled deadlineEnabled();
//
//    boolean isDeadlineEmpty();
//
//    void setDeadlineValid(boolean isValid);
}
