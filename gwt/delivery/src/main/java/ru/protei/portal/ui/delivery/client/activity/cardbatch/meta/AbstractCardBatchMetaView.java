package ru.protei.portal.ui.delivery.client.activity.cardbatch.meta;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.view.PersonProjectMemberView;

import java.util.Date;
import java.util.Set;

public interface AbstractCardBatchMetaView extends IsWidget {

    void setActivity(AbstractCardBatchMetaActivity activity);

    HasValue<CaseState> state();

    HasEnabled stateEnable();

    HasValue<ImportanceLevel> priority();

    HasValue<Date> deadline();

    HasEnabled deadlineEnabled();

    boolean isDeadlineEmpty();

    void setDeadlineValid(boolean isValid);

    HasValue<Set<PersonProjectMemberView>> contractors();
}
